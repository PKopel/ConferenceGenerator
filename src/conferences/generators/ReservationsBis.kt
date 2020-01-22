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


    fun createReservation(clientID: Int): Reservation {
        val conferenceID = Rand.current().nextInt(1,conferences.size+1)
        val noDays = Rand.current().nextInt(1,conferences[conferenceID].days.size)

    }

    private fun createParticipant(conferenceID: Int): Participant {
        val firstName = DataSets.firstNames[currentParticipant]
        val lastName = DataSets.lastNames[currentParticipant]
        val studentCard = if (Rand.current().nextBoolean()) DataSets.studentCards[currentParticipant] else null
        val days = List(Rand.current().nextInt(1,conferences[conferenceID].days.size+1)){

        }
        currentParticipant++
        return Participant()
    }

}