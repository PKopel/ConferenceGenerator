package conferences

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ConferenceReservation(
    private val bookingConferenceID: Int,
    private val conferenceID: Int,
    private val clientID: Int,
    private val bookingDate: LocalDate,
    private val bookingDayList: List<BookingDay>
) {
    fun toSQL(): String {
        val resultBuilder = StringBuilder()
            .append(" PRINT ’Inserting bookings #$bookingConferenceID ’\n")
            .append("SET IDENTITY_INSERT BookingConference ON\n")
            .append(" INSERT INTO BookingConference ( BookingConferenceID , ClientID , ConferenceID, BookingDate, IsCancelled )\n ")
            .append(
                String.format(
                    Locale.US, " VALUES \n\t(%d, %d, %d, ’%s ’, %d)",
                    bookingConferenceID,
                    clientID,
                    conferenceID,
                    bookingDate.format(
                        DateTimeFormatter.ISO_LOCAL_DATE
                    ),
                    0
                )
            )
            .append("\nSET IDENTITY_INSERT BookingConference OFF \n")
        if (bookingDayList.isNotEmpty()) {
            resultBuilder.append("SET IDENTITY_INSERT BookingDay ON\n")
                .append(" INSERT INTO BookingDay ( BookingDayID , ConferenceDayID , BookingConferenceID, NumberOfAttendees, NumberOfStudents, isCancelled )\nVALUES \n ")
            bookingDayList.fold(resultBuilder, { stringBuilder, bookingDay ->
                stringBuilder.append(
                    String.format(
                        Locale.US, "\t(%d, %d, %d, % d, % d, % d)",
                        bookingDay.bookingDayID,
                        bookingDay.conferenceDayID,
                        bookingConferenceID,
                        bookingDay.numberOfAttendees,
                        bookingDay.numberOfStudents,
                        0
                    )
                ).append("\n")
            })

            resultBuilder.append("\nSET IDENTITY_INSERT BookingDay OFF \n")
            if (bookingDayList
                    .flatMap { bookingDay -> bookingDay.bookingWorkshopList }.isNotEmpty()
            ) {
                resultBuilder.append("SET IDENTITY_INSERT BookingWorkshop ON\n")
                    .append(" INSERT INTO BookingWorkshop ( BookingWorkshopID , BookingDayID, WorkshopID, NumberOfAttendees, isCancelled )\nVALUES \n ")
                bookingDayList.flatMap { bookingDay ->
                    bookingDay.bookingWorkshopList
                        .map { bookingWorkshop ->
                            Pair(
                                bookingDay.bookingDayID,
                                bookingWorkshop
                            )
                        }
                }.fold(resultBuilder, { stringBuilder, (first, second) ->
                    stringBuilder.append(
                        String.format(
                            Locale.US, "\t(%d, %d, %d, %d , % d) ",
                            second.bookingWorkshopID,
                            first,
                            second.workshopID,
                            second.numberOfAttendees,
                            0
                        )
                    ).append("\n")
                })
                resultBuilder.append("\nSET IDENTITY_INSERT BookingWorkshop OFF \n")
            }
            if (bookingDayList
                    .flatMap { bookingDay -> bookingDay.reservationDayList }.isNotEmpty()
            ) {
                resultBuilder.append("SET IDENTITY_INSERT ReservationDay ON\n")
                    .append(" INSERT INTO ReservationDay ( ReservationDayID , BookingDayID, AttendeeID, StudentCard )\nVALUES \n ")
                bookingDayList.flatMap { bookingDay ->
                    bookingDay.reservationDayList
                        .map { reservationDay ->
                            Pair(
                                bookingDay.bookingDayID,
                                reservationDay
                            )
                        }
                }.fold(resultBuilder, { stringBuilder, (first, second) ->
                    stringBuilder.append(
                        String.format(
                            Locale.US, "\t(%d, %d, %d, %s)",
                            second.reservationDayID,
                            first,
                            second.attendee.attendeeID,
                            if (second.attendee.studentCard == null) " NULL" else "'" + second.attendee.studentCard + "'"
                        )
                    ).append("\n")
                })

                resultBuilder.append("\nSET IDENTITY_INSERT ReservationDay OFF \n")

                if (bookingDayList
                        .flatMap { bookingDay: BookingDay -> bookingDay.reservationDayList }
                        .flatMap { reservationDay -> reservationDay.reservationWorkshopList }.isNotEmpty()
                ) {
                    resultBuilder.append("SET IDENTITY_INSERT ReservationWorkshop ON\n")
                        .append(" INSERT INTO ReservationWorkshop ( ReservationWorkshopID, ReservationDayID, BookingWorkshopID )\nVALUES \n ")
                    bookingDayList.flatMap { bookingDay ->
                        bookingDay.reservationDayList
                    }.flatMap { reservationDay ->
                        reservationDay.reservationWorkshopList
                            .map { reservationWorkshop ->
                                Pair(
                                    reservationDay.reservationDayID,
                                    reservationWorkshop
                                )
                            }
                    }.fold(resultBuilder, { stringBuilder, (first, second) ->
                        stringBuilder.append(
                            String.format(
                                "\t(%d, %d, %d)",
                                second.reservationWorkshopID, first, second.bookingWorkshopID
                            )
                        ).append("\n")
                    })
                    resultBuilder.append("\nSET IDENTITY_INSERT ReservationWorkshop OFF\n")
                }
            }
        }
        return resultBuilder.toString()
    }
}