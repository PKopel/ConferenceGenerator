package conferences.generators

import conferences.data.DataSets
import conferences.objects.PaymentThreshold
import java.time.LocalDate

object Prices {
    private var priceID = 0

    fun generatePaymentThresholds(conferenceDate: LocalDate): List<PaymentThreshold> {
        val count = Rand.current().nextInt(2, 5)
        val prices = getRandomPrices(count)
        return List(count) { i: Int ->
            PaymentThreshold(
                DataSets.thresholdIDs[priceID++],
                prices[i],
                conferenceDate.minusDays(Rand.current().nextLong(10, 50))
            )
        }
    }

    private fun getRandomPrices(count: Int): List<Double> =
        DataSets.prices.shuffled().take(count).map { price -> price * multiplier }.sorted()


    private val multiplier: Double
        get() = Rand.current().nextDouble(0.8, 1.15)
}