package conferences.objects

import java.time.LocalDate

data class Conference(
    val conferenceID: Int,
    val pricePerDay: Double,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val conferenceName: String,
    val studentPricePercent: Double,
    val conferenceDays: List<ConferenceDay>,
    val paymentThersholds: List<PaymentThershold>
)