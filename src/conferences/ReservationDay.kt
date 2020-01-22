package conferences

import java.util.*

data class ReservationDay(
    val attendee: Attendee,
    val reservationDayID: Int,
    val reservationWorkshopList: MutableList<ReservationWorkshop> = ArrayList()
)