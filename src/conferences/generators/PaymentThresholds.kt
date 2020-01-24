package conferences.generators

import conferences.data.DataSets
import conferences.objects.PaymentThreshold
import java.time.LocalDate

object PaymentThresholds {
    private var priceID = 0

    fun generatePaymentThresholds(conferenceDate: LocalDate): List<PaymentThreshold> {
        val count = Rand.current().nextInt(2, 5)
        val prices = DataSets.prices.take(count).sorted()
        return List(count) { i: Int ->
            PaymentThreshold(
                DataSets.thresholdIDs[priceID++ % DataSets.thresholdIDs.size] + priceID + i,
                prices[i],
                conferenceDate.minusDays(Rand.current().nextLong(10, 50))
            )
        }
    }
}