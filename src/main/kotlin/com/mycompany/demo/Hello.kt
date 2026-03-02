package com.mycompany.demo

import kotlinx.datetime.*
import kotlinx.serialization.json.*
import com.mycompany.demo.toOrdinal

fun main(args: Array<String>) {
    val date = LocalDate(2025, 4, 28)
    val weeklyProper = getWeeklyProper(date)
    val dailyProper = getDailyProper(date)
    val dailyOffice = LiturgicalDay.ofWeek(weeklyProper, date)

    // Check for calendar day
    if (dailyProper != null) {
        val tmp = LiturgicalDay.ofDay(dailyProper, date)
        dailyOffice.morning.merge(tmp.morning)
        dailyOffice.evening.merge(tmp.evening)
    }

    // Check for a vigil tomorrow
    val tomorrow = date.plus(1, DateTimeUnit.DAY)
    val tomorrowWeeklyProper = getWeeklyProper(tomorrow)
    val tomorrowDailyProper = getDailyProper(tomorrow)
    val tomorrowOffice = LiturgicalDay.ofWeek(tomorrowWeeklyProper, tomorrow)
    if (tomorrowOffice.vigil != null) {
        dailyOffice.evening.merge(tomorrowOffice.vigil!!)
    }
    if (tomorrowDailyProper != null) {
        val tmp = LiturgicalDay.ofDay(tomorrowDailyProper, tomorrow)
        if (tmp.vigil != null) {
            dailyOffice.evening.merge(tmp.vigil!!)
        }
    }

    // Check for a Sunday feast
    val yesterday = date.minus(1, DateTimeUnit.DAY)
    val yesterdayDailyProper = getDailyProper(yesterday)
    if (yesterdayDailyProper != null) {
        val tmp = LiturgicalDay.ofDay(yesterdayDailyProper, yesterday)
        if (tmp.morning.rank == Rank.FEAST) {
            dailyOffice.morning.merge(tmp.morning)
            dailyOffice.evening.merge(tmp.evening)
        }
    }

    // Check for Pentecost week
    val easter = computus(date.year)
    val pentecost = easter.plus(7, DateTimeUnit.WEEK)
    if (date > pentecost && date < pentecost.plus(1, DateTimeUnit.WEEK)) {
        val tmp =
                if (date.dayOfWeek == DayOfWeek.WEDNESDAY ||
                                date.dayOfWeek == DayOfWeek.FRIDAY ||
                                date.dayOfWeek == DayOfWeek.SATURDAY
                )
                        Office.SUMMER_EMBER
                else Office.PENTECOST_WEEKDAY
        dailyOffice.morning.merge(tmp)
        dailyOffice.evening.merge(tmp)
    }

    // Check for moved Joseph/Annunciation/Mark
    if (easter.month == Month.MARCH && easter.day <= 26) {
        if (date.dayOfYear - easter.dayOfYear == 8) {
            val tmp = LiturgicalDay.ofDay(getDailyProper(LocalDate(date.year, 3, 19))!!, date)
            dailyOffice.morning.merge(tmp.morning)
            dailyOffice.evening.merge(tmp.evening)
        } else if (date.dayOfYear - easter.dayOfYear == 9) {
            val tmp = LiturgicalDay.ofDay(getDailyProper(LocalDate(date.year, 3, 25))!!, date)
            dailyOffice.morning.merge(tmp.morning)
            dailyOffice.evening.merge(tmp.evening)
        }
    } else if (easter.month == Month.MARCH || (easter.month == Month.APRIL && easter.day == 1)) {
        if (date.dayOfYear - easter.dayOfYear == 8) {
            val tmp = LiturgicalDay.ofDay(getDailyProper(LocalDate(date.year, 3, 25))!!, date)
            dailyOffice.morning.merge(tmp.morning)
            dailyOffice.evening.merge(tmp.evening)
        }
    } else if (easter.day >= 18) {
        if (date.dayOfYear - easter.dayOfYear == 8) {
            val tmp = LiturgicalDay.ofDay(getDailyProper(LocalDate(date.year, 4, 25))!!, date)
            dailyOffice.morning.merge(tmp.morning)
            dailyOffice.evening.merge(tmp.evening)
        }
    }

    // Check for extra bumped Stephen/John
    if (date.month == Month.DECEMBER && date.day == 29) {
        if (date.dayOfWeek == DayOfWeek.WEDNESDAY) {
            val tmp = LiturgicalDay.ofDay(getDailyProper(LocalDate(date.year, 12, 26))!!, date)
            dailyOffice.morning.merge(tmp.morning)
            dailyOffice.evening.merge(tmp.evening)
        } else if (date.dayOfWeek == DayOfWeek.TUESDAY) {
            val tmp = LiturgicalDay.ofDay(getDailyProper(LocalDate(date.year, 12, 27))!!, date)
            dailyOffice.morning.merge(tmp.morning)
            dailyOffice.evening.merge(tmp.evening)
        }
    }

    // Format name
    val sunday = date.minus(date.dayOfWeek.isoDayNumber % 7, DateTimeUnit.DAY)
    val ordinal = toOrdinal(1 + (sunday.dayOfYear - pentecost.dayOfYear) / 7)
    dailyOffice.morning.name = dailyOffice.morning.name.replace("{{pentecost_ordinal}}", ordinal)
    dailyOffice.evening.name = dailyOffice.evening.name.replace("{{pentecost_ordinal}}", ordinal)
    var dayOfWeek = date.dayOfWeek.toString()
    dayOfWeek = dayOfWeek[0] + dayOfWeek.substring(1).lowercase()
    dailyOffice.morning.name = dailyOffice.morning.name.replace("{{day_of_week}}", dayOfWeek)
    dailyOffice.evening.name = dailyOffice.evening.name.replace("{{day_of_week}}", dayOfWeek)

    println("Name: ${dailyOffice.morning.name}")
    println("Morning:")
    println("    Psalter: ${dailyOffice.morning.psalter}")
    println("    First Reading: ${dailyOffice.morning.firstReading}")
    println("    Second Reading: ${dailyOffice.morning.secondReading}")
    println("    Collect: ${dailyOffice.morning.collect}")
    println("Evening:")
    println("    Psalter: ${dailyOffice.evening.psalter}")
    println("    First Reading: ${dailyOffice.evening.firstReading}")
    println("    Second Reading: ${dailyOffice.evening.secondReading}")
    println("    Collect: ${dailyOffice.evening.collect}")
}
