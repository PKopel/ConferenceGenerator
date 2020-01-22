package conferences

import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.stream.Collectors
import java.util.stream.IntStream

class Conferences(private val randomData: RandomData) {
    val conferenceList: MutableList<Conference> = ArrayList()
    private val firstConference = LocalDate.of(2014, 1, 1)
    private val lastConferenceBefore = LocalDate.of(2017, 1, 1)
    private val conferenceInterval = 15
    private val conferenceIntervalDeviation = 5
    private val conferenceMinLength = 1
    private val conferenceMaxLength = 4
    private val minWorkshopsInDay = 1
    private val maxWorkshopsInDay = 7
    private val minPrice = 50.0
    private val maxPrice = 250.0
    private val minStudentPrice = 0.0
    private val maxStudentPrice = 1.0
    private val minDayCapacity = 30
    private val maxDayCapacity = 200
    private val minWorkshopLength = 1
    private val maxWorkshopLength = 4
    private val minWorkshopCapacity = 5
    private val minWorkshopPrice = 0
    private val maxWorkshopPrice = 150
    private var currentDate = firstConference
    private var currentConference = 0
    private var currentConferenceDay = 0
    private var currentWorkshop = 0

    fun generate() {
        while (currentDate < lastConferenceBefore) {
            val conference = createConference()
            conferenceList.add(conference)
        }
    }

    private fun createConference(): Conference {
        val price = ThreadLocalRandom.current().nextDouble(minPrice, maxPrice + 1)
        val length = ThreadLocalRandom.current().nextInt(conferenceMinLength, conferenceMaxLength + 1)
        val endDate = currentDate.plusDays(length - 1.toLong())
        val name = randomData.conferenceNames[currentConference] ?: "nan"
        val studentPrice = ThreadLocalRandom.current().nextDouble(
            minStudentPrice, maxStudentPrice
        )
        val conferenceDays = IntStream.range(0, length)
            .mapToObj { i: Int -> createConferenceDay(i + 1) }
            .collect(Collectors.toList())
        val prices = Prices().generateRandomPrices()
        val conference = Conference(
            currentConference,
            price,
            currentDate,
            endDate,
            name,
            studentPrice,
            conferenceDays,
            prices
        )
        currentConference++
        currentDate = currentDate.plusDays(
            conferenceInterval +
                    ThreadLocalRandom.current().nextInt(
                        -conferenceIntervalDeviation,
                        conferenceIntervalDeviation + 1
                    ).toLong()
        )
        return conference
    }

    private fun createConferenceDay(dayNum: Int?): ConferenceDay {
        val capacity = ThreadLocalRandom.current().nextInt(
            minDayCapacity, maxDayCapacity + 1
        )
        val workshopsNum = ThreadLocalRandom.current().nextInt(
            minWorkshopsInDay, maxWorkshopsInDay + 1
        )

        val conferenceDay = ConferenceDay(
            capacity,
            dayNum,
            currentConferenceDay,
            List(workshopsNum) { createWorkshop(capacity, currentConferenceDay) }
        )
        currentConferenceDay++
        return conferenceDay
    }

    private fun createWorkshop(maxCapacity: Int, conferenceDayID: Int): Workshop {
        val startTime = randomData.randomDayTime
        var endTime = startTime.plusHours(
                ThreadLocalRandom.current().nextInt(
                    minWorkshopLength, maxWorkshopLength + 1
                ).toLong()
            )
        if (endTime.isBefore(startTime)) {
            endTime = LocalTime.of(23, 59)
        }
        val capacity = ThreadLocalRandom.current().nextInt(
            minWorkshopCapacity, maxCapacity
        )
        val price = ThreadLocalRandom.current().nextInt(
            minWorkshopPrice, maxWorkshopPrice + 1
        )
        val workshop = Workshop(
            currentWorkshop,
            randomData.workshopNames[currentWorkshop] ?: "nan",
            capacity,
            startTime,
            endTime,
            price,
            conferenceDayID
        )
        currentWorkshop++
        return workshop
    }

}