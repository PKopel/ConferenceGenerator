package conferences.generators

import conferences.data.DataSets
import conferences.objects.Conference
import conferences.objects.Day
import conferences.objects.Workshop
import java.time.LocalDate
import kotlin.math.round

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

object Conferences {
    val conferenceList: List<Conference> = List(DataSets.conferenceNames.size) { createConference() }.filter { conference -> conference.startDate.isBefore(LocalDate.of(2020, 1, 1)) }

    private var currentDate = LocalDate.of(2017, 1, 1)

    private fun createConference(): Conference {
        currentDate = currentDate?.plusDays(Rand.current().nextInt(0, 31).toLong()) ?: LocalDate.of(2017, 1, 1)
        val length = Rand.current().nextInt(1, 5)
        val name = DataSets.conferenceNames[Conference.counter % DataSets.conferenceNames.size].take(30)
        val discount = Rand.current().nextDouble(0.0, 1.1).round(5)
        val conferenceDays = List(length) { createDay(it) }
        val paymentThresholds = Prices.generatePaymentThresholds(currentDate)
        return Conference(currentDate, name, discount, conferenceDays, paymentThresholds)
    }

    private fun createDay(dayNum: Int): Day {
        val maxParticipants = Rand.current().nextInt(30, 201)
        val workshopsNum = Rand.current().nextInt(1, 8)
        return Day(
            Conference.counter,
            maxParticipants,
            currentDate.plusDays(dayNum.toLong()),
            List(workshopsNum) { createWorkshop(maxParticipants) }
        )
    }

    private fun createWorkshop(maxCapacity: Int): Workshop {
        val startTime = DataSets.randomDayTime
        val duration = Rand.current().nextInt(1, 5)
        val maxParticipants = Rand.current().nextInt(5, maxCapacity)
        val price = Rand.current().nextDouble(0.0, 500.1)
        val name = DataSets.workshopNames[Workshop.counter % DataSets.workshopNames.size].take(50)
        return Workshop(
            name,
            maxParticipants,
            startTime,
            duration,
            price,
            Day.counter
        )
    }
}