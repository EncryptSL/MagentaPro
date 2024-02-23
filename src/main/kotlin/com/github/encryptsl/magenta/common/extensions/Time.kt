package com.github.encryptsl.magenta.common.extensions

import org.apache.commons.lang3.time.DurationFormatUtils
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import java.util.concurrent.TimeUnit


fun Long.parseMinecraftTime(): String {
    var hour = this / 1000L + 6L
    hour %= 24L
    val minute = this % 1000 * 60 / 1000L
    return String.format("%02d:%02d", hour, minute)
}

fun timeFormatPlayer(time: Long): String
{
    val amount: String = DurationFormatUtils.formatDuration(time * 1_000L, "d:H:m:s")
    val parts = amount.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    val days = parts[0]
    val hours = parts[1]
    val minutes = parts[2]
    val sec = parts[3]

    return "$days d $hours h $minutes m $sec s"
}

fun convertUptime(millis: Long): String = String.format(
    "%02d hrs %02d m %02ds", TimeUnit.MILLISECONDS.toHours(millis) % 24,
    TimeUnit.MILLISECONDS.toMinutes(millis) % 60,
    TimeUnit.MILLISECONDS.toSeconds(millis) % 60
)
fun formatFromSecondsTime(seconds: Long): String {
    val timeOfDay = LocalTime.ofSecondOfDay(seconds)
    return timeOfDay.toString()
}

fun datetime(): String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

fun now(): String = LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))

fun convertFromMillis(millis: Long): String {
    val date = Date(millis)
    val from = date.toInstant()
    val localDateTime: LocalDateTime = from.atZone(ZoneId.systemDefault()).toLocalDateTime()
    return DateTimeFormatter.ofPattern("eeee, dd. MMMM yyyy HH:mm:ss", Locale.forLanguageTag("cs")).format(localDateTime)
}

fun convertInstant(instant: Instant): String {
    return DateTimeFormatter.ofPattern("MM-dd hh:mm yyyy").format(instant)
}