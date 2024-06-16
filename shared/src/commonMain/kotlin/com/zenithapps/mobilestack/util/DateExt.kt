package com.zenithapps.mobilestack.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun now(): LocalDateTime = Clock.System.now().toLocalDateTime()

fun Long.toLocalDateTime(): LocalDateTime =
    Instant.fromEpochMilliseconds(this).toLocalDateTime()

fun Instant.toLocalDateTime() = this.toLocalDateTime(TimeZone.currentSystemDefault())
