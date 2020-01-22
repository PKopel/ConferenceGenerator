package conferences

import conferences.objects.Client
import conferences.objects.Conference
import conferences.objects.ConferenceReservation
import conferences.objects.ReservationForDay
import java.time.format.DateTimeFormatter
import java.util.*

fun Client.toSQL(): String = StringBuilder().append("SET IDENTITY_INSERT Clients ON\n")
    .append(" INSERT INTO Clients ( ClientID , Name , Login , Password , IsCompany , Email , Phone )\n")
    .append(" VALUES \n\t( $clientID , '$name' , '$phone' )")
    .append("\nSET IDENTITY_INSERT Clients OFF \n")
    .apply {
        if (participantList.isNotEmpty()) {
            this.append("SET IDENTITY_INSERT Attendees ON\n")
                .append(" INSERT INTO Attendees ( AttendeeID , Name )\nVALUES \n")
                .apply {
                    participantList.fold(this, { stringBuilder, participant ->
                        stringBuilder.append("\t(${participant.attendeeID}, '${participant.name} ')")
                            .append("\n")
                    }).append("\nSET IDENTITY_INSERT Attendees OFF \n")
                }
        }
    }.toString()

fun Conference.toSQL(): String =
    StringBuilder()
        .append("SET IDENTITY_INSERT Conferences ON\n")
        .append(" INSERT INTO Conferences ( ConferenceID , PricePerDay , StartDate , EndDate, ConferenceName, StudentPricePercent )\n ")
        .append(
            String.format(
                Locale.US, " VALUES \n\t(%d, %f, '%s ', '%s ', '%s ', % f) ",
                conferenceID, pricePerDay, startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                endDate.format(DateTimeFormatter.ISO_LOCAL_DATE), conferenceName
                , studentPricePercent
            )
        )
        .append("\nSET IDENTITY_INSERT Conferences OFF \n")
        .apply {
            if (conferenceDays.isNotEmpty()) {
                this.append("SET IDENTITY_INSERT ConferenceDays ON\n")
                    .append(" INSERT INTO ConferenceDays ( ConferenceDayID , Capacity , DayNum, ConferenceID )\nVALUES \n ")
                conferenceDays.fold(this, { stringBuilder, conferenceDay ->
                    stringBuilder.append(
                        String.format(
                            Locale.US,
                            "\t(%d, %d, % d, % d) ",
                            conferenceDay.conferenceDayID,
                            conferenceDay.capacity,
                            conferenceDay.dayNum,
                            conferenceID
                        )
                    ).append("\n")
                })
            }
        }.append("\nSET IDENTITY_INSERT ConferenceDays OFF \n")
        .apply {
            if (conferenceDays
                    .flatMap { conferenceDay -> conferenceDay.workshops }.isNotEmpty()
            ) {
                this.append("SET IDENTITY_INSERT Workshops ON\n")
                    .append(" INSERT INTO Workshops ( WorkshopID , ConferenceDayID , Capacity, StartHour, EndHour, Price, WorkshopName )\nVALUES \n ")
                conferenceDays
                    .flatMap { conferenceDay -> conferenceDay.workshops }
                    .fold(this, { stringBuilder, workshop ->
                        stringBuilder.append(
                            String.format(
                                Locale.US,
                                "\t(%d, %d, %d, '%s ', '%s ', %d, '%s ')",
                                workshop.workshopID, workshop.DayID,
                                workshop.capacity,
                                workshop.startHour.format(DateTimeFormatter.ISO_LOCAL_TIME),
                                workshop.endHour.format(DateTimeFormatter.ISO_LOCAL_TIME),
                                workshop.price, workshop.workshopName
                            )
                        ).append("\n")
                    })
                this.append("\nSET IDENTITY_INSERT Workshops OFF \n")
            }

            if (paymentThersholds.isNotEmpty()) {
                this.append("SET IDENTITY_INSERT Prices ON\n")
                paymentThersholds.fold(this, { stringBuilder, price ->
                    stringBuilder.append(" INSERT INTO Prices ( PriceID , ConferenceID , DayDifference , PricePercent )\nVALUES \n")
                        .append(
                            String.format(
                                Locale.US, "\t(%d, %d, %d, %f)",
                                price.priceID, conferenceID, price.dayDifference, price.pricePercent
                            )
                        ).append("\n")
                })
                this.append("\nSET IDENTITY_INSERT Prices OFF \n")
            }
        }.toString()

fun ConferenceReservation.toSQL(): String = StringBuilder()
    .append("SET IDENTITY_INSERT BookingConference ON\n")
    .append(" INSERT INTO BookingConference ( BookingConferenceID , ClientID , ConferenceID, BookingDate, IsCancelled )\n ")
    .append(
        String.format(
            Locale.US, " VALUES \n\t(%d, %d, %d, '%s ', %d)",
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
    .apply {
        if (reservationForDayList.isNotEmpty()) {
            this.append("SET IDENTITY_INSERT BookingDay ON\n")
                .append(" INSERT INTO BookingDay ( BookingDayID , ConferenceDayID , BookingConferenceID, NumberOfAttendees, NumberOfStudents, isCancelled )\nVALUES \n ")
            reservationForDayList.fold(this, { stringBuilder, bookingDay ->
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

            this.append("\nSET IDENTITY_INSERT BookingDay OFF \n")
            if (reservationForDayList
                    .flatMap { bookingDay -> bookingDay.reservationForWorkshopList }.isNotEmpty()
            ) {
                this.append("SET IDENTITY_INSERT BookingWorkshop ON\n")
                    .append(" INSERT INTO BookingWorkshop ( BookingWorkshopID , BookingDayID, WorkshopID, NumberOfAttendees, isCancelled )\nVALUES \n ")
                reservationForDayList.flatMap { bookingDay ->
                    bookingDay.reservationForWorkshopList
                        .map { bookingWorkshop ->
                            Pair(
                                bookingDay.bookingDayID,
                                bookingWorkshop
                            )
                        }
                }.fold(this, { stringBuilder, (first, second) ->
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
                this.append("\nSET IDENTITY_INSERT BookingWorkshop OFF \n")
            }
            if (reservationForDayList
                    .flatMap { bookingDay -> bookingDay.participantOfDayList }.isNotEmpty()
            ) {
                this.append("SET IDENTITY_INSERT ReservationDay ON\n")
                    .append(" INSERT INTO ReservationDay ( ReservationDayID , BookingDayID, AttendeeID, StudentCard )\nVALUES \n ")
                reservationForDayList.flatMap { bookingDay ->
                    bookingDay.participantOfDayList
                        .map { reservationDay ->
                            Pair(
                                bookingDay.bookingDayID,
                                reservationDay
                            )
                        }
                }.fold(this, { stringBuilder, (first, second) ->
                    stringBuilder.append(
                        String.format(
                            Locale.US, "\t(%d, %d, %d, %s)",
                            second.reservationDayID,
                            first,
                            second.participant.attendeeID,
                            if (second.participant.studentCard == null) " NULL" else "'" + second.participant.studentCard + "'"
                        )
                    ).append("\n")
                })

                this.append("\nSET IDENTITY_INSERT ReservationDay OFF \n")

                if (reservationForDayList
                        .flatMap { reservationForDay: ReservationForDay -> reservationForDay.participantOfDayList }
                        .flatMap { reservationDay -> reservationDay.participantOfWorkshopList }.isNotEmpty()
                ) {
                    this.append("SET IDENTITY_INSERT ReservationWorkshop ON\n")
                        .append(" INSERT INTO ReservationWorkshop ( ReservationWorkshopID, ReservationDayID, BookingWorkshopID )\nVALUES \n ")
                    reservationForDayList.flatMap { bookingDay ->
                        bookingDay.participantOfDayList
                    }.flatMap { reservationDay ->
                        reservationDay.participantOfWorkshopList
                            .map { reservationWorkshop ->
                                Pair(
                                    reservationDay.reservationDayID,
                                    reservationWorkshop
                                )
                            }
                    }.fold(this, { stringBuilder, (first, second) ->
                        stringBuilder.append(
                            String.format(
                                "\t(%d, %d, %d)",
                                second.reservationWorkshopID, first, second.bookingWorkshopID
                            )
                        ).append("\n")
                    })
                    this.append("\nSET IDENTITY_INSERT ReservationWorkshop OFF\n")
                }
            }
        }
    }.toString()

