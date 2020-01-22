package conferences.objects

import java.time.LocalDate

data class ConferenceReservation(
    val bookingConferenceID: Int,
    val conferenceID: Int,
    val clientID: Int,
    val bookingDate: LocalDate,
    val reservationForDayList: List<ReservationForDay>
)