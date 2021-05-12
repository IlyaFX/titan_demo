package ru.ilyafx.titandemo.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import ru.ilyafx.titandemo.config.CalculationConfig
import ru.ilyafx.titandemo.model.CalculationRequestData
import java.time.Duration
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger

@Service
class CalculationService : ICalculationService {

    @Autowired
    lateinit var javascriptService: JavaScriptEvaluationService

    @Autowired
    lateinit var config: CalculationConfig

    fun benchFlux(rangeEnd: Int, fnId: Int, code: String): Flux<BenchRecord> =
        Flux.range(1, rangeEnd).delayElements(config.properties().intervalInMillis)
            .map {
                val start = Instant.now()
                val result = try {
                    javascriptService.eval(code, it)
                } catch (e: Exception) {
                    // TODO: Send a short exception to client
                    null
                }
                BenchRecord(it, fnId, result, Duration.between(start, Instant.now()))
            }

    override fun calculateUnordered(request: CalculationRequestData): Flux<String> {
        var out = Flux.empty<BenchRecord>()
        request.functions.forEachIndexed { i, fn ->
            out = out.mergeWith(benchFlux(request.calculations, i + 1, fn))
        }
        return out.map { it.toString() }
    }

    override fun calculateOrdered(request: CalculationRequestData): Flux<String> {
        val counters = request.functions.indices.map { AtomicInteger() }.toList()
        val iters = request.functions.mapIndexed { i, fn ->
            benchFlux(request.calculations, i + 1, fn).map {
                counters[i].incrementAndGet()
                it
            }
        }.toTypedArray()
        return Flux.zip(iters.asIterable()) { records ->
            val recordsTyped: List<BenchRecord> = records.toList() as List<BenchRecord>
            val iteration = recordsTyped[0].iteration
            ComparisonRecord(iteration, recordsTyped, counters.map {
                it.get() - iteration
            }).toString()
        }
    }
}

fun formatDuration(duration: Duration): String {
    return "${duration.toMillis()}ms"
}

data class BenchRecord(val iteration: Int, val function: Int, val result: Int?, val duration: Duration) {
    fun toShort(): String = "${result}, ${formatDuration(duration)}"
    override fun toString(): String = "${iteration}, ${function}, ${result}, ${formatDuration(duration)}";
}

data class ComparisonRecord(val iteration: Int, val records: List<BenchRecord>, val ahead: List<Int>) {
    override fun toString(): String =
        "${iteration}, ${records.indices.joinToString(", ") { i -> "${records[i].toShort()}, ${ahead[i]}" }}"
}