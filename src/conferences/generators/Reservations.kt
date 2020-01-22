package conferences.generators

import conferences.data.DataSets
import conferences.objects.Client
import conferences.objects.Conference
import conferences.objects.Participant
import conferences.objects.Reservation
import java.util.concurrent.ThreadLocalRandom

typealias Rand = ThreadLocalRandom

object Reservations {
    private val clients: List<Client> = Clients.clientList
    private val conferences: List<Conference> = Conferences.conferenceList

    val reservationList: List<Reservation>
        get() = List(clients.size) {
            List(Rand.current().nextInt(1, conferences.size / 2)) { createReservation(it) }
        }.flatten()

    private var currentParticipant = 0
    private var currentReservation = 0


    private fun createReservation(clientID: Int): Reservation {
        val conference = conferences[Rand.current().nextInt(1, conferences.size + 1)]
        val participants =
            List(Rand.current().nextInt(1, 100)) { createParticipant(conference, currentReservation) }.filterNotNull()
        val days = participants.flatMap { participant -> participant.participantOfDays }
        val workshops = participants.flatMap { participant -> participant.participantOfWorkshops }
        val date = conference.startDate.minusDays(Rand.current().nextLong(1, 50))
        currentReservation++
        return Reservation(date, clientID, days, workshops, participants)
    }

    private fun createParticipant(conference: Conference, reservationID: Int): Participant? {
        val firstName = DataSets.firstNames[currentParticipant]
        val lastName = DataSets.lastNames[currentParticipant]
        val studentCard = if (Rand.current().nextBoolean()) DataSets.studentCards[currentParticipant] else null
        val days = List(Rand.current().nextInt(1, conference.days.size + 1)) {
            conference.days[Rand.current().nextInt(0, conference.days.size)]
        }.toSet().toList().filter { day -> day.maxParticipants > day.occupiedPlaces }
            .map { day -> day.apply { occupiedPlaces++ } }
        val workshops = List(Rand.current().nextInt(1, days.flatMap { day -> day.workshops }.size)) {
            days.flatMap { day -> day.workshops }[Rand.current().nextInt(0, days.flatMap { day -> day.workshops }.size)]
        }.toSet().toList().filter { workshop -> workshop.maxParticipants > workshop.occupiedPlaces }
            .map { workshop -> workshop.apply { occupiedPlaces++ } }
        currentParticipant++
        return if (days.isNotEmpty()) Participant(
            firstName,
            lastName,
            reservationID,
            studentCard,
            days,
            workshops
        ) else null
    }
}