package conferences.objects

data class Client(
    val clientID: Int,
    val name: String?,
    val phone: String?,
    val participantList: List<Participant>
)