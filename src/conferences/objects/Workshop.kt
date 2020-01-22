package conferences.objects

import java.time.LocalTime

class Workshop(
    val workshopName: String,
    val maxParticipants: Int,
    val startTime: LocalTime,
    val duration: Int,
    val price: Double,
    val dayID: Int,
    var occupiedPlaces: Int = 0
)