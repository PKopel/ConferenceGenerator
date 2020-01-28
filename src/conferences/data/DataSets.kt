package conferences.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import conferences.Rand
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.full.memberProperties

object DataSets {

    private val dataMap: HashMap<String, List<String>> = HashMap()
    private val dataIterators: HashMap<String, Iterator<String>> = HashMap()

    init {
        val json = String(Files.readAllBytes(Paths.get("MOCK_DATA.json")))
        val collectionType =
            object : TypeToken<List<DataFormat>>() {}.type
        val dataSet = Gson().fromJson<List<DataFormat>>(json, collectionType)
        for (prop in DataFormat::class.memberProperties) {
            dataMap[prop.name] = dataSet.map { prop.get(it) as String }
            dataIterators[prop.name] = dataMap[prop.name]!!.iterator()
        }
    }

    fun get(property: String): String = when (property) {
        "time" -> LocalTime.of(
            Rand.current().nextInt(8, 21),
            Rand.current().nextInt(0, 60)
        ).format(DateTimeFormatter.ISO_LOCAL_TIME)

        else -> if (dataIterators[property]!!.hasNext()) dataIterators[property]!!.next() else {
            dataIterators[property] = dataMap[property]!!.iterator()
            dataIterators[property]!!.next()
        }
    }

}