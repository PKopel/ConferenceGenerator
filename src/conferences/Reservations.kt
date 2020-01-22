package conferences

import java.lang.Integer.min
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.stream.Collectors
import kotlin.math.max

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
            val conferenceAttendees = getAttendeesListForDay(
                conference
            )
            val dayBookings: MutableList<BookingDay> = ArrayList()
            for (conferenceDay in conference.conferenceDays) {
                val dayAttendees: MutableList<Attendee> = ArrayList()
                val allWorkshopsAttendees: MutableList<Pair<List<Attendee>, Workshop>> =
                    ArrayList()
                val dayCapacityToFill = min(
                    ThreadLocalRandom.current().nextInt(0, conferenceDay.capacity * 2),
                    conferenceDay.capacity
                )
                var currentAttendee = 0
                for (workshop in conferenceDay.workshops) {
                    val workshopCapacityToFill: Int = min(
                        ThreadLocalRandom.current().nextInt(0, workshop.capacity * 2),
                        workshop.capacity
                    )
                    val workshopAttendees: MutableList<Attendee> = ArrayList()
                    while (workshopAttendees.size < workshopCapacityToFill
                        && dayAttendees.size < dayCapacityToFill
                    ) {
                        workshopAttendees.add(
                            conferenceAttendees[currentAttendee]
                        )
                        dayAttendees.add(
                            conferenceAttendees[currentAttendee]
                        )
                        currentAttendee++
                    }
                    allWorkshopsAttendees.add(
                        Pair<List<Attendee>, Workshop>(
                            workshopAttendees,
                            workshop
                        )
                    )
                }
                dayBookings.addAll(
                    createBookingsForDay(
                        conferenceDay,
                        dayAttendees, allWorkshopsAttendees
                    )
                )
            }
            conferenceBookings.addAll(dayBookings
                .groupBy { dayBooking: BookingDay -> dayBooking.clientID }
                .entries.map { entry: Map.Entry<Int, List<BookingDay>> ->
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
        conferenceDay: ConferenceDay,
        dayAttendees: List<Attendee>,
        allWorkshopAttendees: List<Pair<List<Attendee>, Workshop>>
    ): List<BookingDay> {
        val bookingDays = dayAttendees
            .groupBy { attendee: Attendee -> attendee.clientID }
            .entries
            .map { entry: Map.Entry<Int, List<Attendee>> ->
                Pair(
                    Pair(entry.key, currentBookingDay++),
                    entry.value
                )
            }
            .map { (first, second) ->
                BookingDay(
                    first.second,
                    conferenceDay.conferenceDayID,
                    second.filter { attendee: Attendee -> attendee.studentCard == null }.count(),
                    second.filter { attendee: Attendee -> attendee.studentCard != null }.count(),
                    second.map { attendee: Attendee -> ReservationDay(attendee, currentReservationDay++) },
                    first.first
                )
            }
        val attendeeReservationDayMap = bookingDays
            .flatMap { bookingDay: BookingDay -> bookingDay.reservationDayList }
            .map { reservationDay -> Pair(reservationDay.attendee, reservationDay) }.toMap()
        for (bookingDay in bookingDays) {
            val clientID = bookingDay.clientID
            val clientWorkshopAttendees: List<Pair<List<Attendee>, Workshop>> =
                allWorkshopAttendees
                    .map { (first, second) ->
                        Pair<List<Attendee>, Workshop>(
                            first.stream().filter { attendee: Attendee ->
                                attendee.clientID == clientID
                            }.collect(
                                Collectors.toList()
                            ),
                            second
                        )
                    }
                    .filter { (first) -> first.isNotEmpty() }
            val bookingWorkshops = createAndAddBookingWorkshops(
                clientWorkshopAttendees,
                attendeeReservationDayMap
            )
            bookingWorkshops.forEach { bookingWorkshop ->
                bookingDay.bookingWorkshopList.add(bookingWorkshop)
            }
        }
        return bookingDays
    }

    private fun getMaxConferenceDayCapacity(conference: Conference): Int {
        return max(
            conference.conferenceDays.stream()
                .mapToInt { value ->
                    value.workshops.stream()
                        .mapToInt { workshop -> workshop.capacity }
                        .sum()
                }
                .max().orElse(0),
            conference.conferenceDays.stream()
                .mapToInt { value -> value.capacity }
                .max().orElse(0)
        )
    }

    private fun getAttendeesListForDay(conference: Conference): List<Attendee> {
        val maxDayCapacity = getMaxConferenceDayCapacity(conference)
        var currentAttendees = 0
        val clientsForConference: MutableList<Client> = ArrayList()
        while (currentAttendees < maxDayCapacity) {
            val client = clients[ThreadLocalRandom.current().nextInt(0, clients.size)]
            if (!clientsForConference.contains(client)) {
                clientsForConference.add(client)
                currentAttendees += client.attendeeList.size
            }
        }
        return clientsForConference.flatMap { client -> client.attendeeList }
    }

    private fun createAndAddBookingWorkshops(
        allWorkshopAttendees: List<Pair<List<Attendee>, Workshop>>,
        attendeeReservationDayMap: Map<Attendee, ReservationDay>
    ): List<BookingWorkshop> = allWorkshopAttendees.map { (first, second) ->
        Pair(
            BookingWorkshop(
                currentBookingWorkshop++,
                second.workshopID,
                first.size
            ), first
        )
    }.map { (first, second) ->
        Pair(
            first,
            second.map { attendee: Attendee? ->
                ReservationWorkshop(
                    currentReservationWorkshop++,
                    first.bookingWorkshopID,
                    attendee
                )
            })
    }.apply {
        forEach { (_, second) ->
            second.forEach { reservationWorkshop: ReservationWorkshop ->
                attendeeReservationDayMap[reservationWorkshop.attendee]?.reservationWorkshopList?.add(
                    reservationWorkshop
                )
            }
        }
    }.map { pair -> pair.first }

}