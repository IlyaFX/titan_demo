package ru.ilyafx.titandemo.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import ru.ilyafx.titandemo.model.CalculationRequestData
import ru.ilyafx.titandemo.model.CalculationResponseType
import ru.ilyafx.titandemo.services.CalculationService

@RestController
class CalculationController {

    @Autowired
    lateinit var calculationService: CalculationService

    @RequestMapping(
        value = ["/calculator"],
        method = [RequestMethod.POST],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.TEXT_EVENT_STREAM_VALUE]
    )
    fun calculator(@RequestBody request: CalculationRequestData): Flux<String> {
        return if (request.responseType == CalculationResponseType.ON_TIME) {
            calculationService.calculateUnordered(request)
        } else {
            calculationService.calculateOrdered(request)
        }
    }

}