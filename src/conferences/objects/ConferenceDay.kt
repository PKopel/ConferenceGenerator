package conferences.objects

data class ConferenceDay(
    val capacity: Int,
    val dayNum: Int?,
    val conferenceDayID: Int,
    val workshops: List<Workshop>
)