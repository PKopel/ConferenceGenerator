package conferences

import conferences.data.DataSets
import conferences.generators.Clients
import conferences.generators.Conferences
import conferences.generators.Reservations
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant

object Main {
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        println(Instant.now())
        DataSets.read("MOCK_DATA.json")
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