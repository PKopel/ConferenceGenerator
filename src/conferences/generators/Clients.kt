package conferences.generators

import conferences.data.DataSets
import conferences.objects.Client
import conferences.objects.Participant
import java.util.*
import java.util.concurrent.ThreadLocalRandom

class Clients(private val dataSets: DataSets) {
    private var currentClient = 0
    private var currentAttendee = 0
    val clientList: MutableList<Client> = ArrayList()

    fun generate() {
        while (currentAttendee < dataSets.participantNames.size) {
            val client = createClient()
            clientList.add(client)
        }
    }

    private fun createClient(): Client {
        val name = dataSets.companyNames[currentClient]
        val phone = dataSets.phones[currentClient]
        val attendeeCount = ThreadLocalRandom.current().nextInt(
            5,
            100
        )
        val participants = List(attendeeCount) { createAttendee() }.filterNotNull()
        val client = Client(
            currentClient, name, phone, participants
        )
        currentClient++
        return client
    }

    private fun createAttendee(): Participant? {
        if (currentAttendee >= dataSets.participantNames.size) {
            return null
        }
        val name = dataSets.participantNames[currentAttendee]
        val studentCard = dataSets.studentCards[currentAttendee]
        val attendee = Participant(
            name, currentAttendee, studentCard,
            currentClient
        )
        currentAttendee++
        return attendee
    }

}