package com.github.ljtfreitas.julian.samples.kotlin

import com.github.ljtfreitas.julian.Promise
import com.github.ljtfreitas.julian.contract.DELETE
import com.github.ljtfreitas.julian.contract.GET
import com.github.ljtfreitas.julian.contract.JsonContent
import com.github.ljtfreitas.julian.contract.POST
import com.github.ljtfreitas.julian.contract.PUT
import com.github.ljtfreitas.julian.contract.Path
import com.github.ljtfreitas.julian.http.HTTPResponse
import com.github.ljtfreitas.julian.http.HTTPResponseFailure.empty
import com.github.ljtfreitas.julian.http.HTTPStatus
import com.github.ljtfreitas.julian.http.HTTPStatusCode
import com.github.ljtfreitas.julian.k.failure
import com.github.ljtfreitas.julian.k.http
import com.github.ljtfreitas.julian.k.proxy
import com.github.ljtfreitas.julian.k.`when`
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

typealias Id = Int

@Path("/pets")
interface PetsApi {

    // suspend functions will just work as you expect

    @POST
    suspend fun create(@JsonContent pet: NewPet): Pet

    @POST
    fun createAsJob(@JsonContent pet: NewPet): Job // Job is supported as function return

    @POST
    fun createAsPromise(@JsonContent pet: NewPet): Promise<Pet>

    @GET("/{id}")
    suspend fun read(@Path id: Id): Pet? // for possible empty responses, we can use regular Kotlin's nullable notation

    @GET("/{id}")
    fun readAsDeferred(@Path id: Id): Deferred<Pet?> // Deferred is supported as function return (we can expect a nullable result, of course)

    @PUT("/{id}")
    suspend fun update(@Path id: Id, @JsonContent pet: NewPet): HTTPResponse<Pet>

    @DELETE("/{id}")
    suspend fun delete(@Path id: Id): HTTPStatus

    @GET
    suspend fun readAllAsSequence(): Sequence<Pet> // Sequence can be used as function return (as well as kotlin collections)

    @GET
    fun readAllAsFlow(): Flow<Pet> // Flow can be used as function return also
}

@Serializable
data class NewPet(val name: String, val age: Int)

@Serializable
data class Pet(val id: Id, val name: String, val age: Int)

fun main() {
    val petsApi: PetsApi = proxy(endpoint = "http://localhost:7070") {
        http {
            failure {
                `when`(HTTPStatusCode.NOT_FOUND to empty()) // in case a 404 occurs, return null
            }
        }
    }

    runBlocking {
        val newPet = petsApi.create(NewPet(name = "Puka", age = 2))
            .also { pet -> println("a new pet was created: $pet...") }

        petsApi.read(id = newPet.id)
            ?.also { pet -> println("the pet was found: $pet...") }

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
}