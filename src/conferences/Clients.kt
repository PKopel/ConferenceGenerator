package conferences

import java.util.*
import java.util.concurrent.ThreadLocalRandom

class Clients(private val randomData: RandomData) {
    private var currentClient = 0
    private var currentAttendee = 0
    val clientList: MutableList<Client> =  ArrayList()

    fun generate() {
        while (currentAttendee < randomData.participantNames.size) {
            val client = createClient()
            clientList.add(client)
        }
    }

    private fun createClient(): Client {
        val name = randomData.companyNames[currentClient]
        val phone = randomData.phones[currentClient]
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

    private fun createAttendee(): Attendee? {
        if (currentAttendee >= randomData.participantNames.size) {
            return null
        }
        val name = randomData.participantNames[currentAttendee]
        val studentCard = randomData.studentCards[currentAttendee]
        val attendee = Attendee(
            name, currentAttendee, studentCard,
            currentClient
        )
        currentAttendee++
        return attendee
    }

}