package conferences.objects

data class ReservationForWorkshop(
    val bookingWorkshopID: Int,
    val workshopID: Int,
    val numberOfAttendees: Int
)