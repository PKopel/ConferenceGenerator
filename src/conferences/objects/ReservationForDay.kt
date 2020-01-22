package conferences.objects

import java.util.*

class ReservationForDay(
    val bookingDayID: Int,
    val conferenceDayID: Int,
    val numberOfAttendees: Int,
    val numberOfStudents: Int,
    val participantOfDayList: List<ParticipantOfDay>,
    val clientID: Int
) {
    val reservationForWorkshopList: MutableList<ReservationForWorkshop> = ArrayList()
}