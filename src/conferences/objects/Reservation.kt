package conferences.objects

import java.time.LocalDate

data class Reservation(
    val reservationDate: LocalDate,
    val clientID: Int,
    val reservationsForDays: List<Day>,
    val reservationsForWorkshops: List<Workshop>,
    val participants: List<Participant>
)