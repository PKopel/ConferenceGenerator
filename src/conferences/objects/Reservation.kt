package conferences.objects

import java.time.LocalDate

data class Reservation(
    val reservationDate: LocalDate,
    val clientID: Int,
    val reservationsForDays: List<ReservationForDay>,
    val reservationsForWorkshops: List<ReservationForWorkshop>,
    val participants: List<Participant>
)