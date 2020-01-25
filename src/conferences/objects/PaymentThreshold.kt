package conferences.objects

import java.time.LocalDate

class PaymentThreshold(
    val thresholdID: String,
    val price: String,
    val thresholdDate: LocalDate
)