package conferences.generators

import conferences.objects.*
import java.lang.Integer.min
import java.util.*
import java.util.concurrent.ThreadLocalRandom

class Reservations(
    private val clients: List<Client>,
    private val conferences: List<Conference>
) {
    val conferenceBookings: MutableList<ConferenceReservation> = ArrayList()
    private var currentReservationDay = 0
    private var currentReservationWorkshop = 0
    private var currentBookingDay = 0
    private var currentBookingWorkshop = 0
    private var currentConferenceReservation = 0

    fun generate() {
        for (conference in conferences) {
            val conferenceAttendees = getParticipantsListForDay(
                conference
            )
            val dayReservationFors: MutableList<ReservationForDay> = ArrayList()
            for (conferenceDay in conference.days) {
                val dayParticipants: MutableList<Participant> = ArrayList()
                val allWorkshopsAttendees: MutableList<Pair<List<Participant>, Workshop>> =
                    ArrayList()
                val dayCapacityToFill = min(
                    ThreadLocalRandom.current().nextInt(0, conferenceDay.maxParticipants * 2),
                    conferenceDay.maxParticipants
                )
                var currentAttendee = 0
                for (workshop in conferenceDay.workshops) {
                    val workshopCapacityToFill: Int = min(
                        ThreadLocalRandom.current().nextInt(0, workshop.maxParticipants * 2),
                        workshop.maxParticipants
                    )
                    val workshopParticipants: MutableList<Participant> = ArrayList()
                    while (workshopParticipants.size < workshopCapacityToFill
                        && dayParticipants.size < dayCapacityToFill
                    ) {
                        workshopParticipants.add(
                            conferenceAttendees[currentAttendee]
                        )
                        dayParticipants.add(
                            conferenceAttendees[currentAttendee]
                        )
                        currentAttendee++
                    }
                    allWorkshopsAttendees.add(
                        Pair<List<Participant>, Workshop>(
                            workshopParticipants,
                            workshop
                        )
                    )
                }
                dayReservationFors.addAll(
                    createBookingsForDay(
                        conferenceDay,
                        dayParticipants, allWorkshopsAttendees
                    )
                )
            }
            conferenceBookings.addAll(dayReservationFors
                .groupBy { dayReservationFor: ReservationForDay -> dayReservationFor.clientID }
                .entries.map { entry: Map.Entry<Int, List<ReservationForDay>> ->
                ConferenceReservation(
                    currentConferenceReservation++,
                    conference.conferenceID,
                    entry.key,
                    conference.startDate.minusDays(ThreadLocalRandom.current().nextInt(14, 36).toLong()),
                    entry.value
                )
            })
        }
    }

    private fun createBookingsForDay(
        day: Day,
        dayParticipants: List<Participant>,
        allWorkshopAttendees: List<Pair<List<Participant>, Workshop>>
    ): List<ReservationForDay> {
        val bookingDays = dayParticipants
            .groupBy { participant -> participant.clientID }
            .entries
            .map { entry: Map.Entry<Int, List<Participant>> ->
                Pair(
                    Pair(entry.key, currentBookingDay++),
                    entry.value
                )
            }
            .map { (first, second) ->
                ReservationForDay(
                    first.second,
                    day.dayID,
                    second.filter { participant -> participant.studentCard == null }.count(),
                    second.filter { participant -> participant.studentCard != null }.count(),
                    second.map { participant ->
                        ParticipantOfDay(
                            participant,
                            currentReservationDay++
                        )
                    },
                    first.first
                )
            }
        val attendeeReservationDayMap = bookingDays
            .flatMap { reservationForDay -> reservationForDay.participantOfDayList }
            .map { reservationDay -> Pair(reservationDay.participant, reservationDay) }.toMap()
        for (bookingDay in bookingDays) {
            val clientID = bookingDay.clientID
            val clientWorkshopAttendees: List<Pair<List<Participant>, Workshop>> =
                allWorkshopAttendees
                    .map { (first, second) ->
                        Pair(
                            first.filter { participant ->
                                participant.clientID == clientID
                            },
                            second
                        )
                    }
                    .filter { (first) -> first.isNotEmpty() }
            val bookingWorkshops = createAndAddBookingWorkshops(
                clientWorkshopAttendees,
                attendeeReservationDayMap
            )
            bookingWorkshops.forEach { bookingWorkshop ->
                bookingDay.reservationForWorkshopList.add(bookingWorkshop)
            }
        }
        return bookingDays
    }

    private fun getParticipantsListForDay(day: Day): List<Participant> {
        var participants = 0
        val clientsForConference: MutableList<Client> = ArrayList()
        while (participants < day.maxParticipants) {
            val client = clients[ThreadLocalRandom.current().nextInt(0, clients.size)]
            if (!clientsForConference.contains(client)) {
                clientsForConference.add(client)
                participants += client.participantList.size
            }
        }
        return clientsForConference.flatMap { client -> client.participantList }
    }

    private fun createParticipant(): Participant? {
        if (currentParticipant >= dataSets.participantNames.size) {
            return null
        }
        val firstName = dataSets.firstNames[currentParticipant]
        val lastName = dataSets.lastNames[currentParticipant]
        val studentCard = if (ThreadLocalRandom.current().nextBoolean()) dataSets.studentCards[currentParticipant] else null
        val attendee = Participant()
        currentParticipant++
        return attendee
    }


    private fun createAndAddBookingWorkshops(
        allWorkshopAttendees: List<Pair<List<Participant>, Workshop>>,
        participantParticipantOfDayMap: Map<Participant, ParticipantOfDay>
    ): List<ReservationForWorkshop> = allWorkshopAttendees.map { (first, second) ->
        Pair(
            ReservationForWorkshop(
                currentBookingWorkshop++,
                second.workshopID,
                first.size
            ), first
        )
    }.map { (first, second) ->
        Pair(
            first,
            second.map { participant ->
                ParticipantOfWorkshop(
                    currentReservationWorkshop++,
                    first.bookingWorkshopID,
                    participant
                )
            })
    }.apply {
        forEach { (_, second) ->
            second.forEach { participantOfWorkshop ->
                participantParticipantOfDayMap[participantOfWorkshop.participant]?.participantOfWorkshopList?.add(
                    participantOfWorkshop
                )
            }
        }
    }.map { pair -> pair.first }
}