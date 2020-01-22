package conferences.objects

data class Participant(
    val firstName: String?,
    val lastName: String?,
    val reservationID: Int,
    val studentCard: String?,
    val participantOfDays: List<Day>,
    val participantOfWorkshops: List<Workshop>
)