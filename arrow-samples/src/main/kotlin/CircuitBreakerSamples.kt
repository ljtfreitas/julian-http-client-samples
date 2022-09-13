package com.github.ljtfreitas.julian.samples.arrow

import arrow.core.Option
import arrow.fx.coroutines.CircuitBreaker
import com.github.ljtfreitas.julian.k.http
import com.github.ljtfreitas.julian.k.http.arrow.CircuitBreakerHTTPRequestInterceptor
import com.github.ljtfreitas.julian.k.interceptors
import com.github.ljtfreitas.julian.k.proxy
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@DelicateCoroutinesApi
fun main() {

    runBlocking {
        println("running with a circuit breaker...stop the PetsApi server to run this sample")

        val circuitBreaker = CircuitBreaker.of(
            maxFailures = 1,
            resetTimeout = 1000.milliseconds
        )

        val petsApiWithCircuitBreaker: PetsApi = proxy(endpoint = "http://localhost:7070") {
            http {
                interceptors {
                    add(CircuitBreakerHTTPRequestInterceptor(circuitBreaker))
                }
            }
        }

        // it's going to fail
        val firstAttempt: Result<Option<Pet>> = runCatching { petsApiWithCircuitBreaker.read(id = 1) }

        firstAttempt.onSuccess { pet -> println("pet search result: $pet") }
            .onFailure { e -> e.printStackTrace() }

        // now, the circuit breaker is open; this call will be rejected
        val secondAttempt: Result<Option<Pet>> = runCatching { petsApiWithCircuitBreaker.read(id = 1) }

        secondAttempt.onSuccess { pet -> println("pet search result: $pet") }
            .onFailure { e -> e.printStackTrace() }
    }

}