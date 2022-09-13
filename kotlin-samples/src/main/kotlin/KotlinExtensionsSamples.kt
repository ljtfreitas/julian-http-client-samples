package com.github.ljtfreitas.julian.samples.kotlin

import com.github.ljtfreitas.julian.Promise
import com.github.ljtfreitas.julian.http.HTTPResponseFailure.empty
import com.github.ljtfreitas.julian.http.HTTPStatusCode
import com.github.ljtfreitas.julian.k.coroutines.await
import com.github.ljtfreitas.julian.k.coroutines.deferred
import com.github.ljtfreitas.julian.k.coroutines.job
import com.github.ljtfreitas.julian.k.failure
import com.github.ljtfreitas.julian.k.http
import com.github.ljtfreitas.julian.k.plus
import com.github.ljtfreitas.julian.k.proxy
import com.github.ljtfreitas.julian.k.`when`
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking

fun main() {
    val petsApi: PetsApi = proxy(endpoint = "http://localhost:7070") {
        http {
            failure {
                `when`(HTTPStatusCode.NOT_FOUND to empty())
            }
        }
    }

    runBlocking {
        val petAsPromise: Promise<Pet> = petsApi.createAsPromise(NewPet(name = "Puka", age = 2))

        val newPet = petAsPromise.await() // we can await the promise inside a coroutine
            .also { pet -> println("a new pet was created: $pet...") }

        val petAsPromise2: Promise<Pet> = petsApi.createAsPromise(NewPet(name = "Puka", age = 2))
        val petAsJob: Job = petAsPromise2.job() // we can convert the promise to a Job

        petAsJob.join()
        println("a new pet was created, let's continue...")

        val petAsPromise3: Promise<Pet> = petsApi.createAsPromise(NewPet(name = "Puka", age = 2))
        val petAsDeferred: Deferred<Pet> = petAsPromise3.deferred() // we can convert the promise to a Job

        val newPet3 = petAsDeferred.await()
            .also { pet -> println("a new pet was created: $pet...") }

        val petAsPromise4: Promise<Pet> = petsApi.createAsPromise(NewPet(name = "Puka", age = 2))

        petsApi.read(id = 9999)
            .also { pet -> println("the pet ${if (pet == null) "was not found..." else "was found: $pet"}") }

        petsApi.update(id = newPet.id, pet = NewPet(name = newPet.name, age = newPet.age + 1))
            .also { response ->
                response.onSuccess { status, _, pet ->
                    println("the pet was updated: $pet...and the HTTP status was $status.")

                }.onFailure { e -> e.printStackTrace() }

            }

        petsApi.delete(id = newPet.id)
            .let { status -> status to newPet }
            .also { (status, pet) -> println("the pet ${pet.id} was deleted...and the HTTP status was $status.") }
    }

    runBlocking {
        petsApi.createAsJob(NewPet(name = "Puka", age = 2)).join()

        println("a new pet was created...let's continue.")

        petsApi.readAllAsSequence()
            .also { println("all pets: ") }
            .forEach { println(it) }

        petsApi.readAllAsFlow()
            .also { println("all pets: ") }
            .collect { println(it) }

        val pet = petsApi.readAllAsSequence().last()

        val deferredPet = petsApi.readAsDeferred(id = pet.id)
        val deferredNullablePet = petsApi.readAsDeferred(id = 9999)

        println("a deferred pet was found: ${deferredPet.await()}")
        println("a deferred pet was not found: ${deferredNullablePet.await()}")
    }

    runBlocking {

        // Promises can be composed

        val puka: Promise<Pet> = petsApi.createAsPromise(NewPet(name = "Puka", age = 2))
        val hugo: Promise<Pet> = petsApi.createAsPromise(NewPet(name = "Hugo", age = 2))
        val fiona: Promise<Pet> = petsApi.createAsPromise(NewPet(name = "Fiona", age = 1))

        val allPets: Promise<Triple<Pet, Pet, Pet>> = puka + hugo + fiona

        allPets.onSuccess { (pet1, pet2, pet3) -> println("three pets were created: $pet1, $pet2, and $pet3") }
            .join()
    }
}