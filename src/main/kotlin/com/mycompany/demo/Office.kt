package com.mycompany.demo

import kotlinx.datetime.*
import kotlinx.serialization.json.*

class LiturgicalDay(var morning: Office, var evening: Office, var vigil: Office? = null) {
    companion object {
        fun ofDay(day: JsonObject, date: LocalDate): LiturgicalDay {
            val christmas = LocalDate(date.year, 12, 25)
            val firstAdvent =
                    christmas.minus(christmas.dayOfWeek.isoDayNumber + 21, DateTimeUnit.DAY)

            val name = if (day.containsKey("name")) day.get("name")!!.jsonPrimitive.content else ""
            val rank =
                    if (day.containsKey("rank"))
                            Rank.valueOf(day.get("rank")!!.jsonPrimitive.content)
                    else Rank.FERIA
            val morningPsalter =
                    day.get("morning")!!.jsonObject.get("psalter")!!.jsonPrimitive.content
            val eveningPsalter =
                    day.get("evening")!!.jsonObject.get("psalter")!!.jsonPrimitive.content
            val morningReadings =
                    if (day.get("morning")!!.jsonObject.containsKey("readings"))
                            listOf(
                                    day.get("morning")!!.jsonObject.get("readings")!!.jsonArray[0]
                                            .jsonPrimitive
                                            .content,
                                    day.get("morning")!!.jsonObject.get("readings")!!.jsonArray[1]
                                            .jsonPrimitive
                                            .content
                            )
                    else if ((date.year % 2 == 0) xor (date >= firstAdvent))
                            listOf(
                                    day.get("year_2_readings")!!.jsonArray[0].jsonPrimitive.content,
                                    day.get("year_2_readings")!!.jsonArray[2].jsonPrimitive.content
                            )
                    else
                            listOf(
                                    day.get("year_1_readings")!!.jsonArray[0].jsonPrimitive.content,
                                    day.get("year_1_readings")!!.jsonArray[1].jsonPrimitive.content
                            )

            val eveningReadings =
                    if (day.get("evening")!!.jsonObject.containsKey("readings"))
                            listOf(
                                    day.get("evening")!!.jsonObject.get("readings")!!.jsonArray[0]
                                            .jsonPrimitive
                                            .content,
                                    day.get("evening")!!.jsonObject.get("readings")!!.jsonArray[1]
                                            .jsonPrimitive
                                            .content
                            )
                    else if ((date.year % 2 == 0) xor (date >= firstAdvent))
                            listOf(
                                    day.get("year_1_readings")!!.jsonArray[0].jsonPrimitive.content,
                                    day.get("year_2_readings")!!.jsonArray[1].jsonPrimitive.content
                            )
                    else
                            listOf(
                                    day.get("year_2_readings")!!.jsonArray[0].jsonPrimitive.content,
                                    day.get("year_1_readings")!!.jsonArray[2].jsonPrimitive.content
                            )

            val morningCollect =
                    if (day.get("morning")!!.jsonObject.containsKey("collect"))
                            day.get("morning")!!.jsonObject.get("collect")!!.jsonPrimitive.content
                    else if (day.contains("collect")) day.get("collect")!!.jsonPrimitive.content
                    else ""

            val eveningCollect =
                    if (day.get("evening")!!.jsonObject.containsKey("collect"))
                            day.get("evening")!!.jsonObject.get("collect")!!.jsonPrimitive.content
                    else if (day.contains("collect")) day.get("collect")!!.jsonPrimitive.content
                    else ""

            if (day.containsKey("vigil")) {
                val vigilPsalter =
                        day.get("vigil")!!.jsonObject.get("psalter")!!.jsonPrimitive.content
                val vigilReadings =
                        listOf(
                                day.get("vigil")!!.jsonObject.get("readings")!!.jsonArray[0]
                                        .jsonPrimitive
                                        .content,
                                day.get("vigil")!!.jsonObject.get("readings")!!.jsonArray[1]
                                        .jsonPrimitive
                                        .content
                        )
                val vigilCollect =
                        if (day.get("vigil")!!.jsonObject.containsKey("collect"))
                                day.get("vigil")!!.jsonObject.get("collect")!!.jsonPrimitive.content
                        else day.get("collect")!!.jsonPrimitive.content

                return LiturgicalDay(
                        Office(
                                name,
                                rank,
                                Season.NONE,
                                morningPsalter,
                                morningReadings[0],
                                morningReadings[1],
                                morningCollect
                        ),
                        Office(
                                name,
                                rank,
                                Season.NONE,
                                eveningPsalter,
                                eveningReadings[0],
                                eveningReadings[1],
                                eveningCollect
                        ),
                        Office(
                                name,
                                rank,
                                Season.NONE,
                                vigilPsalter,
                                vigilReadings[0],
                                vigilReadings[1],
                                vigilCollect
                        )
                )
            } else {
                return LiturgicalDay(
                        Office(
                                name,
                                rank,
                                Season.NONE,
                                morningPsalter,
                                morningReadings[0],
                                morningReadings[1],
                                morningCollect
                        ),
                        Office(
                                name,
                                rank,
                                Season.NONE,
                                eveningPsalter,
                                eveningReadings[0],
                                eveningReadings[1],
                                eveningCollect
                        )
                )
            }
        }

        fun ofWeek(week: JsonObject, date: LocalDate): LiturgicalDay {
            val christmas = LocalDate(date.year, 12, 25)
            val firstAdvent =
                    christmas.minus(christmas.dayOfWeek.isoDayNumber + 21, DateTimeUnit.DAY)
            val day = week.get(date.dayOfWeek.toString())!!.jsonObject

            val name =
                    if (day.containsKey("name")) day.get("name")!!.jsonPrimitive.content
                    else if (week.containsKey("name")) week.get("name")!!.jsonPrimitive.content
                    else ""
            val rank =
                    if (day.containsKey("rank"))
                            Rank.valueOf(day.get("rank")!!.jsonPrimitive.content)
                    else if (week.containsKey("rank"))
                            Rank.valueOf(week.get("rank")!!.jsonPrimitive.content)
                    else if (date.dayOfWeek == DayOfWeek.SUNDAY) Rank.SUNDAY else Rank.FERIA
            val morningPsalter =
                    day.get("morning")!!.jsonObject.get("psalter")!!.jsonPrimitive.content
            val eveningPsalter =
                    day.get("evening")!!.jsonObject.get("psalter")!!.jsonPrimitive.content
            val morningReadings =
                    if (day.get("morning")!!.jsonObject.containsKey("readings"))
                            listOf(
                                    day.get("morning")!!.jsonObject.get("readings")!!.jsonArray[0]
                                            .jsonPrimitive
                                            .content,
                                    day.get("morning")!!.jsonObject.get("readings")!!.jsonArray[1]
                                            .jsonPrimitive
                                            .content
                            )
                    else if ((date.year % 2 == 0) xor (date >= firstAdvent))
                            listOf(
                                    day.get("year_2_readings")!!.jsonArray[0].jsonPrimitive.content,
                                    day.get("year_2_readings")!!.jsonArray[2].jsonPrimitive.content
                            )
                    else
                            listOf(
                                    day.get("year_1_readings")!!.jsonArray[0].jsonPrimitive.content,
                                    day.get("year_1_readings")!!.jsonArray[1].jsonPrimitive.content
                            )

            val eveningReadings =
                    if (day.get("evening")!!.jsonObject.containsKey("readings"))
                            listOf(
                                    day.get("evening")!!.jsonObject.get("readings")!!.jsonArray[0]
                                            .jsonPrimitive
                                            .content,
                                    day.get("evening")!!.jsonObject.get("readings")!!.jsonArray[1]
                                            .jsonPrimitive
                                            .content
                            )
                    else if ((date.year % 2 == 0) xor (date >= firstAdvent))
                            listOf(
                                    day.get("year_1_readings")!!.jsonArray[0].jsonPrimitive.content,
                                    day.get("year_2_readings")!!.jsonArray[1].jsonPrimitive.content
                            )
                    else
                            listOf(
                                    day.get("year_2_readings")!!.jsonArray[0].jsonPrimitive.content,
                                    day.get("year_1_readings")!!.jsonArray[2].jsonPrimitive.content
                            )
            val morningCollect =
                    if (day.get("morning")!!.jsonObject.containsKey("collect"))
                            day.get("morning")!!.jsonObject.get("collect")!!.jsonPrimitive.content
                    else if (day.containsKey("collect")) day.get("collect")!!.jsonPrimitive.content
                    else week.get("collect")!!.jsonPrimitive.content

            val eveningCollect =
                    if (day.get("evening")!!.jsonObject.containsKey("collect"))
                            day.get("evening")!!.jsonObject.get("collect")!!.jsonPrimitive.content
                    else if (day.containsKey("collect")) day.get("collect")!!.jsonPrimitive.content
                    else week.get("collect")!!.jsonPrimitive.content

            if (day.containsKey("vigil")) {
                val vigilPsalter =
                        day.get("vigil")!!.jsonObject.get("psalter")!!.jsonPrimitive.content
                val vigilReadings =
                        listOf(
                                day.get("vigil")!!.jsonObject.get("readings")!!.jsonArray[0]
                                        .jsonPrimitive
                                        .content,
                                day.get("vigil")!!.jsonObject.get("readings")!!.jsonArray[1]
                                        .jsonPrimitive
                                        .content
                        )
                val vigilCollect =
                        if (day.get("vigil")!!.jsonObject.containsKey("collect"))
                                day.get("vigil")!!.jsonObject.get("collect")!!.jsonPrimitive.content
                        else if (day.containsKey("collect"))
                                day.get("collect")!!.jsonPrimitive.content
                        else week.get("collect")!!.jsonPrimitive.content

                return LiturgicalDay(
                        Office(
                                name,
                                rank,
                                Season.NONE,
                                morningPsalter,
                                morningReadings[0],
                                morningReadings[1],
                                morningCollect
                        ),
                        Office(
                                name,
                                rank,
                                Season.NONE,
                                eveningPsalter,
                                eveningReadings[0],
                                eveningReadings[1],
                                eveningCollect
                        ),
                        Office(
                                name,
                                rank,
                                Season.NONE,
                                vigilPsalter,
                                vigilReadings[0],
                                vigilReadings[1],
                                vigilCollect
                        )
                )
            } else {
                return LiturgicalDay(
                        Office(
                                name,
                                rank,
                                Season.NONE,
                                morningPsalter,
                                morningReadings[0],
                                morningReadings[1],
                                morningCollect
                        ),
                        Office(
                                name,
                                rank,
                                Season.NONE,
                                eveningPsalter,
                                eveningReadings[0],
                                eveningReadings[1],
                                eveningCollect
                        )
                )
            }
        }
    }
}

class Office(
        var name: String,
        var rank: Rank,
        var season: Season,
        var psalter: String,
        var firstReading: String,
        var secondReading: String,
        var collect: String
) {
    companion object {
        val SUMMER_EMBER =
                Office(
                        "Summer Ember Day",
                        Rank.OPTIONAL,
                        Season.PENTECOST,
                        "",
                        "",
                        "",
                        "Almighty and everlasting God, by whose Spirit the whole body of your faithful people is governed and sanctified: Receive our supplications and prayers, which we offer before you for all members of your holy Church, that in their vocation and ministry they may truly and devoutly serve you; through our Lord and Savior Jesus Christ, who lives and reigns with you, in the unity of the Holy Spirit, one God, now and forever. Amen."
                )

        val PENTECOST_WEEKDAY =
                Office(
                        "The {{day_of_week}} after Pentecost",
                        Rank.OPTIONAL,
                        Season.PENTECOST,
                        "",
                        "",
                        "",
                        ""
                )

        val THANKSGIVING_MORNING = Office(
            "Thanksgiving",
            Rank.OPTIONAL,
            Season.PENTECOST,
            "147",
            "Deut. 26:1-11",
            "John 6:26-35",
            "Almighty and gracious Father, we give you thanks for the fruits of the earth in their season and for the labors of those who harvest them. Make us, we pray, faithful stewards of your great bounty, for the provision of our necessities and the relief of all who are in need, to the glory of your Name; through Jesus Christ our Lord, who lives and reigns with you and the Holy Spirit, one God, now and for ever. Amen."
        )

        val THANKSGIVING_EVENING = Office(
            "Thanksgiving",
            Rank.OPTIONAL,
            Season.PENTECOST,
            "145",
            "Joel 2:21-27",
            "1 Thess. 5:12-24",
            "Almighty and gracious Father, we give you thanks for the fruits of the earth in their season and for the labors of those who harvest them. Make us, we pray, faithful stewards of your great bounty, for the provision of our necessities and the relief of all who are in need, to the glory of your Name; through Jesus Christ our Lord, who lives and reigns with you and the Holy Spirit, one God, now and for ever. Amen."
        )

        val LABOR_DAY = Office(
            "Labor Day",
            Rank.OPTIONAL,
            Season.PENTECOST,
            "",
            "",
            "",
            "Almighty God, you have so linked our lives one with another that all we do affects, for good or ill, all other lives: So guide us in the work we do, that we may do it not for self alone, but for the common good; and, as we seek a proper return for our own labor, make us mindful of the rightful aspirations of other workers, and arouse our concern for those who are out of work; through Jesus Christ our Lord, who lives and reigns with you and the Holy Spirit, one God, for ever and ever. Amen."
        )
    }

    fun merge(other: Office) {
        if (this.name.isBlank() || (other.name.isNotBlank() && other.rank > this.rank)) {
            this.name = other.name
        }
        if (this.season == Season.NONE || (other.season != Season.NONE && other.rank > this.rank)) {
            this.season = other.season
        }
        if (this.psalter.isBlank() || (other.psalter.isNotBlank() && other.rank > this.rank)) {
            this.psalter = other.psalter
        }
        if (this.firstReading.isBlank() ||
                        (other.firstReading.isNotBlank() && other.rank > this.rank)
        ) {
            this.firstReading = other.firstReading
        }
        if (this.secondReading.isBlank() ||
                        (other.secondReading.isNotBlank() && other.rank > this.rank)
        ) {
            this.secondReading = other.secondReading
        }
        if (this.collect.isBlank() || (other.collect.isNotBlank() && other.rank > this.rank)) {
            this.collect = other.collect
        }
        if (other.rank > this.rank) {
            this.rank = other.rank
        }
    }
}

enum class Season {
    NONE,
    ADVENT,
    CHRISTMAS,
    EPIPHANY,
    LENT,
    EASTER,
    PENTECOST
}

enum class Rank {
    NONE,
    FERIA,
    OPTIONAL,
    FEAST,
    SUNDAY,
    PRINCIPAL
}
