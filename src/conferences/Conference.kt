package conferences

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class Conference(
    val conferenceID: Int,
    private val pricePerDay: Double,
    val startDate: LocalDate,
    private val endDate: LocalDate,
    private val conferenceName: String,
    private val studentPricePercent: Double,
    val conferenceDays: List<ConferenceDay>,
    private val prices: List<Price>
) {
    fun toSQL(): String =
        StringBuilder()
            .append(" PRINT ’Inserting conference #$conferenceID ’\n")
            .append("SET IDENTITY_INSERT Conferences ON\n")
            .append(" INSERT INTO Conferences ( ConferenceID , PricePerDay , StartDate , EndDate, ConferenceName, StudentPricePercent )\n ")
            .append(
                String.format(
                    Locale.US, " VALUES \n\t(%d, %f, ’%s ’, ’%s ’, ’%s ’, % f) ",
                    conferenceID, pricePerDay, startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    endDate.format(DateTimeFormatter.ISO_LOCAL_DATE), conferenceName
                    , studentPricePercent
                )
            )
            .append("\nSET IDENTITY_INSERT Conferences OFF \n")
            .apply {
                if (conferenceDays.isNotEmpty()) {
                    this.append("SET IDENTITY_INSERT ConferenceDays ON\n")
                        .append(" INSERT INTO ConferenceDays ( ConferenceDayID , Capacity , DayNum, ConferenceID )\nVALUES \n ")
                    conferenceDays.fold(this, { stringBuilder, conferenceDay ->
                        stringBuilder.append(
                            String.format(
                                Locale.US,
                                "\t(%d, %d, % d, % d) ",
                                conferenceDay.conferenceDayID,
                                conferenceDay.capacity,
                                conferenceDay.dayNum,
                                conferenceID
                            )
                        ).append("\n")
                    })
                }
            }.append("\nSET IDENTITY_INSERT ConferenceDays OFF \n")
            .apply {
                if (conferenceDays
                        .flatMap { conferenceDay -> conferenceDay.workshops }.isNotEmpty()
                ) {
                    this.append("SET IDENTITY_INSERT Workshops ON\n")
                        .append(" INSERT INTO Workshops ( WorkshopID , ConferenceDayID , Capacity, StartHour, EndHour, Price, WorkshopName )\nVALUES \n ")
                    conferenceDays
                        .flatMap { conferenceDay -> conferenceDay.workshops }
                        .fold(this, { stringBuilder, workshop ->
                            stringBuilder.append(
                                String.format(
                                    Locale.US,
                                    "\t(%d, %d, %d, ’%s ’, ’%s ’, %d, ’%s ’)",
                                    workshop.workshopID, workshop.DayID,
                                    workshop.capacity,
                                    workshop.startHour.format(DateTimeFormatter.ISO_LOCAL_TIME),
                                    workshop.endHour.format(DateTimeFormatter.ISO_LOCAL_TIME),
                                    workshop.price, workshop.workshopName
                                )
                            ).append("\n")
                        })
                    this.append("\nSET IDENTITY_INSERT Workshops OFF \n")
                }

                if (prices.isNotEmpty()) {
                    this.append("SET IDENTITY_INSERT Prices ON\n")
                    prices.fold(this, { stringBuilder, price ->
                        stringBuilder.append(" INSERT INTO Prices ( PriceID , ConferenceID , DayDifference , PricePercent )\nVALUES \n")
                            .append(
                                String.format(
                                    Locale.US, "\t(%d, %d, %d, %f)",
                                    price.priceID, conferenceID, price.dayDifference, price.pricePercent
                                )
                            ).append("\n")
                    })
                    this.append("\nSET IDENTITY_INSERT Prices OFF \n")
                }
            }.toString()

}