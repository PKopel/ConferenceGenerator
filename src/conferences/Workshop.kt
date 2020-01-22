package conferences

import java.time.LocalTime

class Workshop(
    val workshopID: Int,
    val workshopName: String,
    val capacity: Int,
    val startHour: LocalTime,
    val endHour: LocalTime,
    val price: Int,
    val DayID: Int
)