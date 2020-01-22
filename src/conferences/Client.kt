package conferences

import java.util.*
import java.util.stream.Collectors

class Client(
    val clientID: Int,
    val name: String?,
    val phone: String?,
    val attendeeList: List<Attendee>
) {
    fun toSQL(): String {
        val resultBuilder = StringBuilder()
        resultBuilder.append(" PRINT ’Inserting client #$clientID ’\n")
            .append("SET IDENTITY_INSERT Clients ON\n")
            .append(" INSERT INTO Clients ( ClientID , Name , Login , Password , IsCompany , Email , Phone )\n")
            .append(
                String.format(
                    Locale.US,
                    " VALUES \n\t(%d, ’%s ’, ’%s ’)",
                    clientID, name, phone
                )
            )
            .append("\nSET IDENTITY_INSERT Clients OFF \n")
        if (attendeeList.isNotEmpty()) {
            resultBuilder.append("SET IDENTITY_INSERT Attendees ON\n")
                .append(" INSERT INTO Attendees ( AttendeeID , Name )\nVALUES \n")
                .append(attendeeList.stream()
                    .map { attendee: Attendee ->
                        String.format(
                            "\t(%d, ’%s ’)",
                            attendee.attendeeID, attendee.name
                        )
                    }
                    .collect(Collectors.joining(" ,\n")))
                .append("\nSET IDENTITY_INSERT Attendees OFF \n")
        }
        return resultBuilder.toString()
    }

}