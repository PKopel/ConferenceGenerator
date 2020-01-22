package conferences

import conferences.objects.Client
import conferences.objects.Conference
import conferences.objects.ConferenceReservation
import conferences.objects.ReservationForDay
import java.time.format.DateTimeFormatter

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
                        stringBuilder.append("\t(${participant.attendeeID}, '${participant.name} ') \n")
                    })
                }
                .append("\nSET IDENTITY_INSERT Attendees OFF \n")
        }
    }.toString()

fun Conference.toSQL(): String =
    StringBuilder()
        .append("SET IDENTITY_INSERT Conferences ON\n")
        .append(" INSERT INTO Conferences ( ConferenceID , PricePerDay , StartDate , EndDate, ConferenceName, StudentPricePercent )\n ")
        .append(
            " VALUES \n\t($conferenceID, $pricePerDay, '${
            startDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
            }' ,'${
            endDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
            }' , '$conferenceName' , $discount"
        )
        .append("\nSET IDENTITY_INSERT Conferences OFF \n")
        .apply {
            if (days.isNotEmpty()) {
                this.append("SET IDENTITY_INSERT ConferenceDays ON\n")
                    .append(" INSERT INTO ConferenceDays ( ConferenceDayID , Capacity , DayNum, ConferenceID )\nVALUES \n ")
                days.fold(this, { stringBuilder, conferenceDay ->
                    stringBuilder.append(
                        "\t( ${conferenceDay.dayID}, ${conferenceDay.maxParticipants}, ${conferenceDay.dayNum}, $conferenceID) \n"
                    )
                })
            }
        }.append("\nSET IDENTITY_INSERT ConferenceDays OFF \n")
        .apply {
            if (days.flatMap { conferenceDay -> conferenceDay.workshops }.isNotEmpty()) {
                this.append("SET IDENTITY_INSERT Workshops ON\n")
                    .append(" INSERT INTO Workshops ( WorkshopID , ConferenceDayID , Capacity, StartHour, EndHour, Price, WorkshopName )\nVALUES \n ")
                days
                    .flatMap { conferenceDay -> conferenceDay.workshops }
                    .fold(this, { stringBuilder, workshop ->
                        stringBuilder.append(
                            "\t(${workshop.workshopID}, ${workshop.DayID}, ${workshop.maxParticipants}, '${
                            workshop.startTime.format(DateTimeFormatter.ISO_LOCAL_TIME)}', '${
                            workshop.endHour.format(DateTimeFormatter.ISO_LOCAL_TIME)}', ${
                            workshop.price}, '${workshop.workshopName}') \n"
                        )
                    })
                this.append("\nSET IDENTITY_INSERT Workshops OFF \n")
            }

            if (paymentThresholds.isNotEmpty()) {
                this.append("SET IDENTITY_INSERT Prices ON\n")
                paymentThresholds.fold(this, { stringBuilder, price ->
                    stringBuilder.append(" INSERT INTO Prices ( PriceID , ConferenceID , DayDifference , PricePercent )\nVALUES \n")
                        .append("\t( ${price.thresholdID}, ${conferenceID}, ${price.dayDifference}, ${price.pricePercent}) \n")
                })
                this.append("\nSET IDENTITY_INSERT Prices OFF \n")
            }
        }.toString()

fun ConferenceReservation.toSQL(): String = StringBuilder()
    .append("SET IDENTITY_INSERT BookingConference ON\n")
    .append(" INSERT INTO BookingConference ( BookingConferenceID , ClientID , ConferenceID, BookingDate, IsCancelled )\n ")
    .append(" VALUES \n\t($bookingConferenceID, $clientID, $conferenceID, ${bookingDate.format(DateTimeFormatter.ISO_LOCAL_DATE)}, 0 )")
    .append("\nSET IDENTITY_INSERT BookingConference OFF \n")
    .apply {
        if (reservationForDayList.isNotEmpty()) {
            this.append("SET IDENTITY_INSERT BookingDay ON\n")
                .append(" INSERT INTO BookingDay ( BookingDayID , ConferenceDayID , BookingConferenceID, NumberOfAttendees, NumberOfStudents, isCancelled )\nVALUES \n ")
            reservationForDayList.fold(this, { stringBuilder, bookingDay ->
                stringBuilder.append(
                    "\t(${bookingDay.reservationID}, ${bookingDay.dayID}, ${
                    bookingConferenceID}, ${bookingDay.numberOfParticipants}, ${bookingDay.numberOfStudents}, 0 ) \n "
                )
            })

            this.append("\nSET IDENTITY_INSERT BookingDay OFF \n")
            if (reservationForDayList.flatMap { bookingDay -> bookingDay.reservationForWorkshopList }.isNotEmpty()) {
                this.append("SET IDENTITY_INSERT BookingWorkshop ON\n")
                    .append(" INSERT INTO BookingWorkshop ( BookingWorkshopID , BookingDayID, WorkshopID, NumberOfAttendees, isCancelled )\nVALUES \n ")
                reservationForDayList.flatMap { bookingDay ->
                    bookingDay.reservationForWorkshopList
                        .map { bookingWorkshop -> Pair(bookingDay.reservationID, bookingWorkshop) }
                }.fold(this, { stringBuilder, (first, second) ->
                    stringBuilder.append("\t(${second.bookingWorkshopID}, ${first}, ${second.workshopID}, ${second.numberOfAttendees}, 0 ) \n")
                })
                this.append("\nSET IDENTITY_INSERT BookingWorkshop OFF \n")
            }
            if (reservationForDayList.flatMap { bookingDay -> bookingDay.participantOfDayList }.isNotEmpty()) {
                this.append("SET IDENTITY_INSERT ReservationDay ON\n")
                    .append(" INSERT INTO ReservationDay ( ReservationDayID , BookingDayID, AttendeeID, StudentCard )\nVALUES \n ")
                reservationForDayList.flatMap { bookingDay ->
                    bookingDay.participantOfDayList
                        .map { reservationDay -> Pair(bookingDay.reservationID, reservationDay) }
                }.fold(this, { stringBuilder, (first, second) ->
                    stringBuilder.append(
                        "\t(${second.dayID}, ${first}, ${second.participant.attendeeID}, ${
                        if (second.participant.studentCard == null) " NULL" else "'" + second.participant.studentCard + "'"} ) \n"
                    )
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
                            .map { reservationWorkshop -> Pair(reservationDay.dayID, reservationWorkshop) }
                    }.fold(this, { stringBuilder, (first, second) ->
                        stringBuilder.append("\t(${second.reservationID}, $first, ${second.workshopID}) \n")
                    })
                    this.append("\nSET IDENTITY_INSERT ReservationWorkshop OFF\n")
                }
            }
        }
    }.toString()

