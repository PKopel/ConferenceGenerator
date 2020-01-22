package conferences

import java.util.concurrent.ThreadLocalRandom

class Prices {
    fun generateRandomPrices(): List<Price> {
        val count = ThreadLocalRandom.current().nextInt(2, 5)
        val days = getRandomDays(count)
        val prices = getRandomPrices(count)
        return List(count) { i: Int -> Price(priceID++, prices[i], days[i]) }
    }

    private fun getRandomPrices(count: Int): List<Double> =
        PRICES.toMutableList().shuffled().take(count).map { price -> price * multiplier }.sorted()


    private fun getRandomDays(count: Int): List<Int> =
        DAYS.toMutableList().shuffled().take(count).map { price -> (price * multiplier).toInt() }.sortedDescending()

    private val multiplier: Double
        get() = ThreadLocalRandom.current().nextDouble(0.8, 1.15)

    companion object {
        private val PRICES = listOf(0.25, 0.36, 0.55, 0.85)

        private val DAYS = listOf(10, 20, 30, 50)

        private var priceID = 0 // ugly
    }
}