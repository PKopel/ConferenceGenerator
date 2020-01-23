package conferences.objects

import java.time.LocalDate

data class Day(
    val ConferenceID: Int,
    val maxParticipants: Int,
    val dayDate: LocalDate,
    val workshops: List<Workshop>,
    var occupiedPlaces: Int = 0
){
    val dayID = counter++

    companion object {
        var counter = 0
    }
}