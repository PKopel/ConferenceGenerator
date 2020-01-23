package conferences

import conferences.objects.Client
import conferences.objects.Conference
import conferences.objects.Reservation
import java.time.format.DateTimeFormatter

fun Client.toSQL(): String = StringBuilder()
    .apply {
        if (company != null) {
            this.append(" exec add_company_client '$phone', '$email', '$clientAddress', '${company.NIP}', '${company.companyName}', '${company.address}' \n")
        } else {
            this.append(" exec add_individual_client '$phone', '$email', '$clientAddress' \n")
        }
    }.toString()

fun Conference.toSQL(): String =
    StringBuilder()
        .append(" INSERT INTO Conference ( ConferenceName, Discount )\n ")
        .append(" VALUES \n\t('$conferenceName', $discount) \n")
        .apply {
            if (days.isNotEmpty()) {
                this.append("\n INSERT INTO Day ( ConferenceID , DayDate, MaxParticipants )\nVALUES")
                days.fold(this, { stringBuilder, conferenceDay ->
                    stringBuilder.append(
                        "\n\t( $conferenceID, '${conferenceDay.dayDate}', ${conferenceDay.maxParticipants}),"
                    )
                })
            }

            if (days.flatMap { conferenceDay -> conferenceDay.workshops }.isNotEmpty()) {
                this.append("\n INSERT INTO Workshop ( DayID , MaxParticipants, StartTime, Duration, Price, WorkshopName )\nVALUES")
                days.flatMap { conferenceDay -> conferenceDay.workshops }
                    .fold(this, { stringBuilder, workshop ->
                        stringBuilder.append(
                            "\n\t(${workshop.dayID}, ${workshop.maxParticipants}, '${
                            workshop.startTime.format(DateTimeFormatter.ISO_LOCAL_TIME)}', ${
                            workshop.duration}, ${
                            workshop.price}, '${workshop.workshopName}'),"
                        )
                    })
            }

            if (paymentThresholds.isNotEmpty()) {
                this.append("\n INSERT INTO PaymentThresholds ( ThresholdID , ConferenceID , ThresholdDate , Price )\nVALUES")
                paymentThresholds.fold(this, { stringBuilder, threshold ->
                    stringBuilder.append(
                        "\n\t( '${threshold.thresholdID}', ${conferenceID}, '${threshold.thresholdDate.format(
                            DateTimeFormatter.ISO_LOCAL_DATE
                        )}', ${threshold.price}),"
                    )
                })
            }
        }.toString().replace("),\n I", ")\n I").removeSuffix(",")

fun Reservation.toSQL(): String = StringBuilder()
    .append(" INSERT INTO Reservation ( ClientID , ReservationDate)\n ")
    .append(" VALUES \n\t( $clientID, '${reservationDate.format(DateTimeFormatter.ISO_LOCAL_DATE)}')")
    .apply {
        if (reservationsForDays.isNotEmpty()) {
            this.append("\n INSERT INTO ReservationForDay ( ReservationID , DayID , NumberOfParticipants, NumberOfStudents )\nVALUES")
            reservationsForDays.fold(this, { stringBuilder, day ->
                stringBuilder.append(
                    "\n\t($reservationID, ${day.dayID}, ${
                    dayNumberOfParticipants(day)}, ${dayNumberOfStudents(day)} ),"
                )
            })
        }

        if (reservationsForWorkshops.isNotEmpty()) {
            this.append("\n INSERT INTO ReservationForWorkshop ( ReservationID , WorkshopID, NumberOfParticipants, NumberOfStudents  )\nVALUES")
            reservationsForWorkshops.fold(this, { stringBuilder, workshop ->
                stringBuilder.append(
                    "\n\t($reservationID, ${workshop.workshopID}, ${
                    workshopNumberOfParticipants(workshop)}, ${workshopNumberOfStudents(workshop)}),"
                )
            })
        }

        if (participants.isNotEmpty()) {
            this.append("\n INSERT INTO Participant ( ReservationID , FirstName, LastName, StudentCardNumber, StudentCardValidityDate )\nVALUES")
            participants.fold(this, { stringBuilder, participant ->
                stringBuilder.append(
                    "\n\t($reservationID, '${participant.firstName}', '${participant.lastName}', ${
                    if (participant.studentCardNumber == null) " NULL, NULL" else "'${participant.studentCardNumber}', '${participant.studentCardValidityDate}'"} ),"
                )
            })

            participants.forEach {
                if (it.participantOfDays.isNotEmpty()) {
                    this.append("\n INSERT INTO ParticipantOfDay (ReservationID, ParticipantID, DayID)\n VALUES")
                    it.participantOfDays.fold(this, { stringBuilder, day ->
                        stringBuilder.append(
                            "\n\t($reservationID, ${it.participantID}, ${day.dayID} ),"
                        )
                    })
                }
                if (it.participantOfWorkshops.isNotEmpty()) {
                    this.append("\n INSERT INTO ParticipantOfWorkshop (ReservationID, ParticipantID, WorkshopID)\n VALUES")
                    it.participantOfWorkshops.fold(this, { stringBuilder, workshop ->
                        stringBuilder.append(
                            "\n\t($reservationID, ${it.participantID}, ${workshop.workshopID} ),"
                        )
                    })
                }
            }
        }
    }.toString().replace("),\n I", ")\n I").removeSuffix(",")

