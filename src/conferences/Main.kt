package conferences

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant

object Main {
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        println(Instant.now())
        val randomData = RandomData()
        randomData.read(" MOCK_DATA . json ")
        val conferences = Conferences(randomData)
        conferences.generate()
        val clients = Clients(randomData)
        clients.generate()
        val bookings = Reservations(
            clients.clientList,
            conferences.conferenceList
        )
        bookings.generate()
        val generatedBuilder = StringBuilder().append("USE konferencje \n")
            .append("GO\n")
            .append(" -- remove previous data from database \n")
            .append(" EXEC sp_MSForEachTable ’DISABLE TRIGGER ALL ON ? ’\n")
            .append("GO\n")
            .append(" EXEC sp_MSForEachTable ’ALTER TABLE ? NOCHECK CONSTRAINT ALL ’\n ")
            .append("GO\n")
            .append(" EXEC sp_MSForEachTable ’DELETE FROM ? ’\n")
            .append("GO\n")
            .append(" EXEC sp_MSForEachTable ’ALTER TABLE ? CHECK CONSTRAINT ALL ’\n")
            .append("GO\n")
            .append(" EXEC sp_MSForEachTable ’ENABLE TRIGGER ALL ON ? ’\n")
            .append("GO\n")
            .apply {
                conferences.conferenceList.fold(
                    this,
                    { builder, conference -> builder.append(conference.toSQL()).append("\n") })
            }
            .append("\n\n")
            .apply { clients.clientList.fold(this, { builder, client -> builder.append(client.toSQL()).append("\n") }) }
            .append("\n\n")
            .apply {
                bookings.conferenceBookings.fold(
                    this,
                    { builder, conferenceReservation -> builder.append(conferenceReservation.toSQL()).append("\n") })
            }
        Files.write(Paths.get(" generated .sql"), generatedBuilder.toString().toByteArray())
        println(Instant.now())
    }
}