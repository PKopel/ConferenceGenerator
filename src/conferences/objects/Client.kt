package conferences.objects

data class Client(
    val email: String,
    val phone: String,
    val clientAddress: String,
    val company: Company?
)