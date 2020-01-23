package conferences.objects

data class Participant(
    val firstName: String?,
    val lastName: String?,
    val reservationID: Int,
    val studentCardNumber: String?,
    val studentCardValidityDate: String?,
    val participantOfDays: List<Day>,
    val participantOfWorkshops: List<Workshop>
){
    val participantID = counter++

    companion object {
        var counter = 0
    }
}