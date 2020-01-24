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

    val reservationList: List<Reservation> = List(clients.size) {
        List(Rand.current().nextInt(1, conferences.size / 2)) { createReservation(it) }
    }.flatten()

    private fun createReservation(clientID: Int): Reservation {
        val conference = conferences[Rand.current().nextInt(0, conferences.size)]
        val participants =
            List(Rand.current().nextInt(1, 100)) { createParticipant(conference, Reservation.counter) }.filterNotNull()
        val days = participants.flatMap { participant -> participant.participantOfDays }.toSet().toList()
        val workshops = participants.flatMap { participant -> participant.participantOfWorkshops }.toSet().toList()
        val date = conference.startDate.minusDays(Rand.current().nextLong(1, 50))
        return Reservation(date, clientID, days, workshops, participants)
    }

    private fun createParticipant(conference: Conference, reservationID: Int): Participant? {
        val firstName = DataSets.firstNames[Participant.counter % DataSets.firstNames.size]
        val lastName = DataSets.lastNames[Participant.counter % DataSets.lastNames.size]
        val studentCard =
            if (Rand.current().nextBoolean()) DataSets.studentCards[Participant.counter % DataSets.studentCards.size] else null
        val studentCardValidityDate =
            if (studentCard != null) DataSets.dates[Participant.counter % DataSets.dates.size] else null
        val days = List(Rand.current().nextInt(1, conference.days.size + 1)) {
            conference.days[Rand.current().nextInt(0, conference.days.size)]
        }.toSet().toList().filter { day -> day.maxParticipants > day.occupiedPlaces }
            .map { day -> day.apply { occupiedPlaces++ } }
        val workshops = (
                if (days.flatMap { day -> day.workshops }.size > 1)
                    List(Rand.current().nextInt(1, days.flatMap { day -> day.workshops }.size)) {
                        days.flatMap { day -> day.workshops }[Rand.current().nextInt(0, days.flatMap { day -> day.workshops }.size)]
                    }.toSet().toList()
                else
                    days.flatMap { day -> day.workshops }).filter { workshop -> workshop.maxParticipants > workshop.occupiedPlaces }
            .map { workshop -> workshop.apply { occupiedPlaces++ } }
        return if (days.isNotEmpty()) Participant(
            firstName,
            lastName,
            reservationID,
            studentCard,
            studentCardValidityDate,
            days,
            workshops
        ) else null
    }
}