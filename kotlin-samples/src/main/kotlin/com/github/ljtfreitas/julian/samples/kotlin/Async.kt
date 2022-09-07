package com.github.ljtfreitas.julian.samples.kotlin

import com.github.ljtfreitas.julian.Promise
import com.github.ljtfreitas.julian.contract.GET
import com.github.ljtfreitas.julian.k.coroutines.await
import com.github.ljtfreitas.julian.k.plus
import com.github.ljtfreitas.julian.k.proxy
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    val httpBin = proxy<AsyncHTTPBin>(endpoint = "https://httpbin.org")

    runBlocking {
        println("GET: ${httpBin.get()}")
        println("#############")

        println("GET: ${httpBin.asyncGet().await()}")
        println("#############")

        val first = httpBin.getAsPromise()
        val second = httpBin.getAsPromise()

        val composed = first + second

        composed.onSuccess { (a, b) ->
            println("first GET: $a")
            println("#############")

            println("second GET: $b")
            println("#############")
        }

        delay(2000)
    }
}

interface AsyncHTTPBin {

    @GET("/get")
    suspend fun get(): String

    @GET("/get")
    fun asyncGet(): Deferred<String>

    @GET("/get")
    fun getAsPromise(): Promise<String>

}