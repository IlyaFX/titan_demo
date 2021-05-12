package ru.ilyafx.titandemo.model

import org.springframework.boot.convert.DurationUnit
import java.time.Duration
import java.time.temporal.ChronoUnit

data class CalculationProperties(@DurationUnit(ChronoUnit.MILLIS) var intervalInMillis: Duration = Duration.ofMillis(1000))