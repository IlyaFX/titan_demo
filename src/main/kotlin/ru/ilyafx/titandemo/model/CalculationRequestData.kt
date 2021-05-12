package ru.ilyafx.titandemo.model

data class CalculationRequestData(
    val functions: List<String>,
    val calculations: Int,
    val responseType: CalculationResponseType
)