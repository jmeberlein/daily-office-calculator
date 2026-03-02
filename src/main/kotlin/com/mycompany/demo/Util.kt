package com.mycompany.demo

import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.*
import kotlinx.serialization.json.*
import java.io.File

val ADVENT_PROPERS = fileToJsonElement(File("/home/jmeberlein/git/my-maven-sample-app/src/main/resources/advent.json"))!!.jsonArray
val CHRISTMAS_PROPERS = fileToJsonElement(File("/home/jmeberlein/git/my-maven-sample-app/src/main/resources/christmas.json"))!!.jsonArray
val EPIPHANY_PROPERS = fileToJsonElement(File("/home/jmeberlein/git/my-maven-sample-app/src/main/resources/epiphany.json"))!!.jsonArray
val LENT_PROPERS = fileToJsonElement(File("/home/jmeberlein/git/my-maven-sample-app/src/main/resources/lent.json"))!!.jsonArray
val EASTER_PROPERS = fileToJsonElement(File("/home/jmeberlein/git/my-maven-sample-app/src/main/resources/easter.json"))!!.jsonArray
val PENTECOST_PROPERS = fileToJsonElement(File("/home/jmeberlein/git/my-maven-sample-app/src/main/resources/pentecost.json"))!!.jsonArray
val DAILY_PROPERS = fileToJsonElement(File("/home/jmeberlein/git/my-maven-sample-app/src/main/resources/saints.json"))!!.jsonObject

fun fileToJsonElement(file: File): JsonElement? {
    try {
        // Step 1: Read the file content into a string
        val jsonString: String = file.readText(Charsets.UTF_8) // Specify character encoding

        // Step 2: Parse the JSON string into a JsonElement
        // You can use the default Json configuration or a custom one
        val json = Json { ignoreUnknownKeys = true } // Example of a custom configuration
        val jsonElement: JsonElement = json.parseToJsonElement(jsonString)
        
        return jsonElement
    } catch (e: Exception) {
        // Handle exceptions such as file not found, permission issues, or JSON parsing errors
        e.printStackTrace()
        return null
    }
}

fun getWeeklyProper(date: LocalDate): JsonObject {
    val sunday = date.minus(date.dayOfWeek.isoDayNumber % 7, DateTimeUnit.DAY)
    val epiphany = LocalDate(date.year, 1, 6)
    val epiphanyZero = epiphany.minus(epiphany.dayOfWeek.isoDayNumber % 7, DateTimeUnit.DAY)
    val easter = computus(date.year)
    val quinquagesima = easter.minus(7, DateTimeUnit.WEEK)
    val pentecost = easter.plus(7, DateTimeUnit.WEEK)
    val trinity = pentecost.plus(1, DateTimeUnit.WEEK)
    val christmas = LocalDate(date.year, 12, 25)
    val christmasZero = christmas.minus(christmas.dayOfWeek.isoDayNumber % 7, DateTimeUnit.DAY)
    val firstAdvent = christmas.minus(christmas.dayOfWeek.isoDayNumber + 21, DateTimeUnit.DAY)

    if (date < epiphany) {
        val prevChristmas = LocalDate(date.year - 1, 12, 25)
        val prevChristmasZero = prevChristmas.minus(prevChristmas.dayOfWeek.isoDayNumber % 7, DateTimeUnit.DAY)
        val index = (sunday.day - prevChristmasZero.day + 31) / 7
        return CHRISTMAS_PROPERS[index].jsonObject
    } else if (date < quinquagesima) {
        val index = (sunday.dayOfYear - epiphanyZero.dayOfYear) / 7
        return EPIPHANY_PROPERS[index].jsonObject
    } else if (date < easter) {
        val index = 7 - (easter.dayOfYear - sunday.dayOfYear) / 7
        return LENT_PROPERS[index].jsonObject
    } else if (date <= pentecost || date == trinity) {
        val index = (sunday.dayOfYear - easter.dayOfYear) / 7
        return EASTER_PROPERS[index].jsonObject
    } else if (date < firstAdvent) {
        val index = 29 - (firstAdvent.dayOfYear - sunday.dayOfYear) / 7
        return PENTECOST_PROPERS[index].jsonObject
    } else if (date < christmas) {
        val index = (sunday.dayOfYear - firstAdvent.dayOfYear) / 7
        return ADVENT_PROPERS[index].jsonObject
    } else {
        val index = (sunday.dayOfYear - christmasZero.dayOfYear) / 7
        return CHRISTMAS_PROPERS[index].jsonObject
    }

}

fun getDailyProper(date: LocalDate): JsonObject? {
    val formatter = LocalDate.Format { byUnicodePattern("MMdd") }
    val key = date.format(formatter)
    return DAILY_PROPERS.get(key)?.jsonObject
}

fun computus(year: Int): LocalDate {
    val a = year % 19
    val b = year / 100
    val c = year % 100
    val d = b / 4
    val e = b % 4
    val f = (b + 8) / 25
    val g = (b - f + 1) / 3
    val h = (19 * a + b - d - g + 15) % 30
    val i = c / 4
    val k = c % 4
    val l = (32 + 2 * e + 2 * i - h - k) % 7
    val m = (a + 11 * h + 22 * l) / 451
    val n = (h + l - 7 * m + 114) / 31
    val o = (h + l - 7 * m + 114) % 31
    return LocalDate(year, n, o + 1)
}

fun toOrdinal(n: Int): String {
    return when (n) {
        1 -> "First"
        2 -> "Second"
        3 -> "Third"
        4 -> "Fourth"
        5 -> "Fifth"
        6 -> "Sixth"
        7 -> "Seventh"
        8 -> "Eighth"
        9 -> "Ninth"
        10 -> "Tenth"
        11 -> "Eleventh"
        12 -> "Twelfth"
        13 -> "Thirteenth"
        14 -> "Fourteenth"
        15 -> "Fifteenth"
        16 -> "Sixteenth"
        17 -> "Seventeenth"
        18 -> "Eighteenth"
        19 -> "Nineteenth"
        20 -> "Twentieth"
        21 -> "Twenty-First"
        22 -> "Twenty-Second"
        23 -> "Twenty-Third"
        24 -> "Twenty-Fourth"
        25 -> "Twenty-Fifth"
        26 -> "Twenty-Sixth"
        27 -> "Twenty-Seventh"
        28 -> "Twenty-Eighth"
        29 -> "Twenty-Ninth"
        30 -> "Thirtieth"
        31 -> "Thirty-First"
        32 -> "Thirty-Second"
        33 -> "Thirty-Third"
        34 -> "Thirty-Fourth"
        else -> "N-th"
    }
}