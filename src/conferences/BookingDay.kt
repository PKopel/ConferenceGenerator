package conferences

import java.util.*

class BookingDay(
    val bookingDayID: Int,
    val conferenceDayID: Int,
    val numberOfAttendees: Int,
    val numberOfStudents: Int,
    val reservationDayList: List<ReservationDay>,
    val clientID: Int
) {
    val bookingWorkshopList: MutableList<BookingWorkshop>

    fun addBookingWorkshop(bookingWorkshop: BookingWorkshop) {
        bookingWorkshopList.add(bookingWorkshop)
    }

    init {
        bookingWorkshopList = ArrayList()
    }
}