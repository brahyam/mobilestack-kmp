package com.zenithapps.mobilestack.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

fun now(): LocalDateTime = Clock.System.now().toLocalDateTime()

fun Long.toLocalDateTime(): LocalDateTime =
    Instant.fromEpochMilliseconds(this).toLocalDateTime()

fun Instant.toLocalDateTime() = this.toLocalDateTime(TimeZone.currentSystemDefault())

fun Long.toLocalDate(): LocalDate =
    Instant.fromEpochMilliseconds(this).toLocalDateTime().date

fun LocalDate.toMillis(): Long =
    this.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
