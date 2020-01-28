package conferences

import conferences.generators.Clients
import conferences.generators.Conferences
import conferences.generators.Reservations
import conferences.objects.Client
import conferences.objects.Conference
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant
import java.util.concurrent.ThreadLocalRandom


typealias Rand = ThreadLocalRandom

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}

object Main {
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        println(Instant.now())
        var conferenceList: List<Conference>? = null
        var clientList: List<Client>? = null

        coroutineScope {
            launch {
                conferenceList = Conferences.conferenceList()
            }
            launch {
                clientList = Clients.clientList()
            }
        }

        val generatedBuilder = StringBuilder()
            .apply {
                conferenceList?.fold(
                    this,
                    { builder, conference -> builder.append(conference.toSQL()).append("\n") })
            }
            .append("\n\n")
            .apply { clientList?.fold(this, { builder, client -> builder.append(client.toSQL()).append("\n") }) }
            .append("\n\n")
            .apply {
                Reservations(clientList!!, conferenceList!!).reservationList().fold(
                    this,
                    { builder, conferenceReservation -> builder.append(conferenceReservation.toSQL()).append("\n") })
            }
        Files.write(Paths.get("mock_data.sql"), generatedBuilder.toString().toByteArray())
        println(Instant.now())
    }
}