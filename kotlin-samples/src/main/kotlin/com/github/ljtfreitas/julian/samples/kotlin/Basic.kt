package com.github.ljtfreitas.julian.samples.kotlin

import com.github.ljtfreitas.julian.k.proxy

fun main(args: Array<String>) {
    val httpBin = proxy<HTTPBin>(endpoint = "https://httpbin.org")

    println("GET: ${httpBin.get()}")
    println("#############")

    println("POST: ${httpBin.post("hello")}")
    println("#############")

    println("PUT: ${httpBin.put("hello")}")
    println("#############")

    println("DELETE: ${httpBin.delete()}")
    println("#############")
}