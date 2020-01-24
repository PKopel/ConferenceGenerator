package conferences.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import conferences.generators.Rand
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalTime

object DataSets {
    private var dataSet: List<DataFormat>? = null

    val conferenceNames: List<String>
        get() = twoPartsString({ it.conf_name }, { it.conf_name })

    val workshopNames: List<String>
        get() = twoPartsString({ it.conf_name }, { it.conf_name })

    val firstNames: List<String>
        get() = dataSet!!.map { it.first_name }

    val lastNames: List<String>
        get() = dataSet!!.map { it.last_name }

    val studentCards: List<String>
        get() = dataSet!!.map { it.student_card }

    val companyNames: List<String>
        get() = dataSet!!.map { it.company_name }

    val addresses: List<String>
        get() = dataSet!!.map { it.address }

    val nips: List<String>
        get() = dataSet!!.map { it.NIP }

    val emails: List<String>
        get() = dataSet!!.map { it.email }

    val phones: List<String>
        get() = dataSet!!.map { it.phone }

    val thresholdIDs: List<String>
        get() = dataSet!!.map { it.threshold_id }

    val prices: List<Double>
        get() = dataSet!!.map { it.price }.shuffled()

    val dates: List<String>
        get() = dataSet!!.map { it.date }

    val time: LocalTime
        get() = LocalTime.of(
            Rand.current().nextInt(8, 21),
            Rand.current().nextInt(0, 60)
        )

    @Throws(IOException::class)
    fun read(path: String) {
        val json = String(Files.readAllBytes(Paths.get(path)))
        val collectionType =
            object : TypeToken<List<DataFormat>>() {}.type
        dataSet = Gson().fromJson<List<DataFormat>>(json, collectionType)
    }

    private fun twoPartsString(
        getPart1: (format: DataFormat) -> String,
        getPart2: (format: DataFormat) -> String
    ): List<String> {
        val parts1 = dataSet!!.map { getPart1(it) }.shuffled()
        val parts2 = dataSet!!.map { getPart2(it) }.shuffled()
        return List(parts1.size) { "${parts1[it]} ${parts2[it]}" }
    }
}