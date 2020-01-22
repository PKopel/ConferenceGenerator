package conferences.generators

import conferences.data.DataSets
import conferences.objects.Client
import conferences.objects.Conference
import conferences.objects.Participant
import conferences.objects.Reservation
import java.util.concurrent.ThreadLocalRandom

typealias Rand = ThreadLocalRandom

class ReservationsBis(
    private val clients: List<Client>,
    private val conferences: List<Conference>
) {
    val reservations: List<Reservation>
        get() = List(clients.size) {
            List(Rand.current().nextInt(1, conferences.size / 2)) { createReservation(it) }
        }.flatten()
    private var currentReservationDay = 0
    private var currentReservationWorkshop = 0
    private var currentBookingDay = 0
    private var currentBookingWorkshop = 0
    private var currentConferenceReservation = 0
    private var currentParticipant = 0
    private var currentReservation = 0


    fun createReservation(clientID: Int): Reservation {
        val conference = conferences[Rand.current().nextInt(1, conferences.size + 1)]
        val participants = List(Rand.current().nextInt(1,100)){createParticipant(conference,currentReservation)}.filterNotNull()
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
        return if (days.isNotEmpty()) Participant(firstName, lastName, reservationID, studentCard, days, workshops) else null
    }

}