package conferences.generators

import conferences.data.DataSets
import conferences.objects.Conference
import conferences.objects.Day
import conferences.objects.Workshop
import java.time.LocalDate

object Conferences {
    val conferenceList: List<Conference>
        get() = List(DataSets.conferenceNames.size) { createConference() }.filter { conference -> conference.startDate < lastDate }

    private val lastDate = LocalDate.of(2020, 1, 1)
    private var currentDate = LocalDate.of(2017, 1, 1)
    private var currentConference = 0
    private var currentConferenceDay = 0
    private var currentWorkshop = 0

    private fun createConference(): Conference {
        currentDate = currentDate.plusDays(Rand.current().nextInt(0, 31).toLong())
        val length = Rand.current().nextInt(1, 5)
        val name = DataSets.conferenceNames[currentConference]
        val discount = Rand.current().nextDouble(0.0, 1.1)
        val conferenceDays = List(length) { createDay(it) }
        val paymentThresholds = Prices.generatePaymentThresholds(currentDate)
        currentConference++
        return Conference(currentDate, name, discount, conferenceDays, paymentThresholds)
    }

    private fun createDay(dayNum: Int): Day {
        val maxParticipants = Rand.current().nextInt(30, 201)
        val workshopsNum = Rand.current().nextInt(1, 8)
        currentConferenceDay++
        return Day(
            currentConference,
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
        val name = DataSets.workshopNames[currentWorkshop]
        currentWorkshop++
        return Workshop(
            name,
            maxParticipants,
            startTime,
            duration,
            price,
            currentConferenceDay
        )
    }
}