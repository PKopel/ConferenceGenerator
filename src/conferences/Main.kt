package conferences

import conferences.generators.Clients
import conferences.generators.Conferences
import conferences.generators.Reservations
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
    fun main(args: Array<String>) {
        println(Instant.now())
        val generatedBuilder = StringBuilder()
            .apply {
                Conferences.conferenceList.fold(
                    this,
                    { builder, conference -> builder.append(conference.toSQL()).append("\n") })
            }
            .append("\n\n")
            .apply { Clients.clientList.fold(this, { builder, client -> builder.append(client.toSQL()).append("\n") }) }
            .append("\n\n")
            .apply {
                Reservations.reservationList.fold(
                    this,
                    { builder, conferenceReservation -> builder.append(conferenceReservation.toSQL()).append("\n") })
            }
        Files.write(Paths.get("mock_data.sql"), generatedBuilder.toString().toByteArray())
        println(Instant.now())
    }
}