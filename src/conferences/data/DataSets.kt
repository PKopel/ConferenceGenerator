package conferences.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import conferences.generators.Rand
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalTime

object DataSets {
    private var data: List<DataFormat>? = null

    private val multiplier: Double
        get() = Rand.current().nextDouble(0.8, 1.15)

    val conferenceNames: List<String>
        get() = twoPartsString({ dataFormat -> dataFormat.conf_name }, { dataFormat -> dataFormat.conf_name })

    val workshopNames: List<String>
        get() = twoPartsString({ dataFormat -> dataFormat.conf_name }, { dataFormat -> dataFormat.conf_name })

    val firstNames: List<String>
        get() = data!!.map { dataFormat -> dataFormat.first_name.replace('\'', ' ') }

    val lastNames: List<String>
        get() = data!!.map { dataFormat -> dataFormat.last_name.replace('\'', ' ') }

    val studentCards: List<String>
        get() = data!!.map { dataFormat -> dataFormat.student_card.replace('\'', ' ') }

    val companyNames: List<String>
        get() = data!!.map { dataFormat -> dataFormat.company_name.replace('\'', ' ') }

    val addresses: List<String>
        get() = data!!.map { dataFormat -> dataFormat.company_address.replace('\'', ' ') }

    val nips: List<String>
        get() = data!!.map { dataFormat -> dataFormat.NIP }

    val emails: List<String>
        get() = data!!.map { dataFormat -> dataFormat.email.replace('\'', ' ') }

    val phones: List<String>
        get() = data!!.map { dataFormat -> dataFormat.phone.replace('\'', ' ') }

    val thresholdIDs: List<String>
        get() = data!!.map { dataFormat -> dataFormat.threshold_id.replace('\'', ' ') }

    val prices: List<Double>
        get() = data!!.map { dataFormat -> dataFormat.price }.shuffled().map { price -> price* multiplier }

    val dates: List<String>
        get() = data!!.map { dataFormat -> dataFormat.date }

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
        data = Gson().fromJson<List<DataFormat>>(json, collectionType)
    }

    private fun twoPartsString(
        getPart1: (format: DataFormat) -> String,
        getPart2: (format: DataFormat) -> String
    ): List<String> {
        val parts1 = data!!.map { getPart1(it).replace('\'', ' ') }.shuffled()
        val parts2 = data!!.map { getPart2(it).replace('\'', ' ') }.shuffled()
        return List(parts1.size) { "${parts1[it]} ${parts2[it]}" }
    }
}