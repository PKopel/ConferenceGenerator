package conferences.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalTime
import java.util.concurrent.ThreadLocalRandom

class DataSets {
    private var data: List<DataFormat>? = null

    val conferenceNames: List<String>
        get() = twoPartsString({ dataFormat -> dataFormat.conf_name }, { dataFormat -> dataFormat.conf_name })

    val workshopNames: List<String>
        get() = twoPartsString({ dataFormat -> dataFormat.conf_name }, { dataFormat -> dataFormat.conf_name })

    val participantNames: List<String>
        get() = twoPartsString({ dataFormat -> dataFormat.first_name }, { dataFormat -> dataFormat.last_name })

    val firstNames: List<String>
        get() = data!!.map { dataFormat -> dataFormat.first_name.replace('\'', ' ') }

    val lastNames: List<String>
        get() = data!!.map { dataFormat -> dataFormat.last_name.replace('\'', ' ') }

    val clientAddresses: List<String>
        get() = data!!.map { dataFormat -> dataFormat.client_address.replace('\'', ' ') }

    val studentCards: List<String>
        get() = data!!.map { dataFormat -> dataFormat.student_card.replace('\'', ' ') }

    val companyNames: List<String>
        get() = data!!.map { dataFormat -> dataFormat.company_name.replace('\'', ' ') }

    val companyAddresses: List<String>
        get() = data!!.map { dataFormat -> dataFormat.company_address.replace('\'', ' ') }

    val NIPs: List<String>
        get() = data!!.map { dataFormat -> dataFormat.NIP }

    val emails: List<String>
        get() = data!!.map { dataFormat -> dataFormat.email.replace('\'', ' ') }

    val phones: List<String>
        get() = data!!.map { dataFormat -> dataFormat.phone.replace('\'', ' ') }

    val randomDayTime: LocalTime
        get() = LocalTime.of(
            ThreadLocalRandom.current().nextInt(8, 21),
            ThreadLocalRandom.current().nextInt(0, 60)
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
        val parts1 = data!!.map { getPart1(it).replace('\'', ' ') }.toMutableList()
        val parts2 = data!!.map { getPart2(it).replace('\'', ' ') }.toMutableList()
        parts1.shuffle()
        parts2.shuffle()
        return List(parts1.size) { "${parts1[it]} ${parts2[it]}" }
    }
}