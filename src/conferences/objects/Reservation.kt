package conferences.objects

import java.time.LocalDate

data class Reservation(
    val reservationDate: LocalDate,
    val clientID: Int,
    val reservationsForDays: List<Day>,
    val reservationsForWorkshops: List<Workshop>,
    val participants: List<Participant>
) {
    val reservationID = counter++

    fun dayNumberOfParticipants(day: Day): Int =
        participants.filter { participant -> participant.participantOfDays.contains(day) }.size

    fun dayNumberOfStudents(day: Day): Int = participants.filter { participant ->
        participant.studentCardNumber != null && participant.participantOfDays.contains(day)
    }.count()


    fun workshopNumberOfParticipants(workshop: Workshop): Int =
        participants.filter { participant -> participant.participantOfWorkshops.contains(workshop) }.size

    fun workshopNumberOfStudents(workshop: Workshop): Int = participants.filter { participant ->
        participant.studentCardNumber != null && participant.participantOfWorkshops.contains(workshop)
    }.count()

    companion object {
        var counter = 0
    }
}