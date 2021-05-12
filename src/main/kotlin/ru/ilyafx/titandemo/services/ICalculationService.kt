package ru.ilyafx.titandemo.services

import reactor.core.publisher.Flux
import ru.ilyafx.titandemo.model.CalculationRequestData

interface ICalculationService {

    fun calculateUnordered(request: CalculationRequestData): Flux<String>
    fun calculateOrdered(request: CalculationRequestData): Flux<String>
}