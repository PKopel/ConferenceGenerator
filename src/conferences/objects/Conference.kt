package conferences.objects

import java.time.LocalDate

data class Conference(
    val startDate: LocalDate,
    val conferenceName: String,
    val discount: Double,
    val days: List<Day>,
    val paymentThresholds: List<PaymentThreshold>
){
    val conferenceID = counter++

    companion object {
        var counter = 0
    }
}