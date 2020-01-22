package conferences

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalTime
import java.util.concurrent.ThreadLocalRandom

class RandomData {
    private var data: List<ApiData>? = null
    @Throws(IOException::class)
    fun read(path: String) {
        val json = String(Files.readAllBytes(Paths.get(path)))
        val collectionType =
            object : TypeToken<List<ApiData>>() {}.type
        data = Gson().fromJson<List<ApiData>>(json, collectionType)
    }

    val conferenceNames: List<String>
        get() = twoPartsString({ apiData -> apiData.conf_name }, { apiData -> apiData.conf_name })

    val workshopNames: List<String>
        get() = twoPartsString({ apiData -> apiData.conf_name }, { apiData -> apiData.conf_name })

    val participantNames: List<String>
        get() = twoPartsString({ apiData -> apiData.first_name }, { apiData -> apiData.last_name })

    val studentCards: List<String?>
        get() = data!!.map { apiData -> apiData.student_card }

    val companyNames: List<String?>
        get() = data!!.map { apiData -> apiData.company_name }

    val emails: List<String?>
        get() = data!!.map { apiData -> apiData.email }

    val phones: List<String?>
        get() = data!!.map { apiData -> apiData.phone }

    val randomDayTime: LocalTime
        get() = LocalTime.of(
            ThreadLocalRandom.current().nextInt(8, 21),
            ThreadLocalRandom.current().nextInt(0, 60)
        )

    fun twoPartsString(getPart1: (api: ApiData) -> String, getPart2: (api: ApiData) -> String): List<String> {
        val parts1 = data!!.map { getPart1(it) }.toMutableList()
        val parts2 = data!!.map { getPart2(it) }.toMutableList()
        parts1.shuffle()
        parts2.shuffle()
        return List(parts1.size) { "${parts1[it]} ${parts2[it]}" }
    }
}