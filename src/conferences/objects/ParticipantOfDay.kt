package conferences.objects

import java.util.*

data class ParticipantOfDay(
    val participant: Participant,
    val reservationDayID: Int,
    val participantOfWorkshopList: MutableList<ParticipantOfWorkshop> = ArrayList()
)