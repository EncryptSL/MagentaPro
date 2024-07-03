package com.github.encryptsl.magenta.common.extensions

import kotlinx.datetime.Clock
import org.apache.commons.lang3.time.DateFormatUtils
import org.apache.commons.lang3.time.DurationFormatUtils
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
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

fun datetime(): String = DateFormatUtils.format(Clock.System.now().toEpochMilliseconds(), "HH:mm")

fun now(): String = LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))

fun convertFromMillis(millis: Long): String {
    return DateFormatUtils.format(Date(millis), "eeee, dd. MMMM yyyy HH:mm:ss", Locale.forLanguageTag("cs"))
}

fun convertInstant(instant: Instant, format: String): String {
    return DateFormatUtils.format(instant.toEpochMilli(), format)
}