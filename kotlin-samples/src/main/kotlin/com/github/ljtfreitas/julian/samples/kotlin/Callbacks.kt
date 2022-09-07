package com.github.ljtfreitas.julian.samples.kotlin

import com.github.ljtfreitas.julian.k.KFunctionCallbackResponseT
import com.github.ljtfreitas.julian.k.proxy
import com.github.ljtfreitas.julian.k.responses

fun main(args: Array<String>) {
    val httpBin = proxy<HTTPBin>(endpoint = "https://httpbin.org") {
        responses {
            add(KFunctionCallbackResponseT)
        }
    }

    httpBin.get { result ->
        println("GET: $result")
        println("#############")
    }

    httpBin.post("hello") { result ->
        println("POST: $result")
        println("#############")
    }

    httpBin.put("hello") { result ->
        println("PUT: $result")
        println("#############")
    }

    httpBin.delete { result ->
        println("DELETE: $result")
        println("#############")
    }

    Thread.sleep(2000)
}
