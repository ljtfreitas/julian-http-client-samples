package com.github.ljtfreitas.julian.samples.arrow

import arrow.core.Either
import arrow.core.Option
import arrow.core.continuations.Effect
import arrow.core.continuations.result
import arrow.core.flatMap
import com.github.ljtfreitas.julian.contract.DELETE
import com.github.ljtfreitas.julian.contract.GET
import com.github.ljtfreitas.julian.contract.JsonContent
import com.github.ljtfreitas.julian.contract.POST
import com.github.ljtfreitas.julian.contract.PUT
import com.github.ljtfreitas.julian.contract.Path
import com.github.ljtfreitas.julian.http.HTTPResponse
import com.github.ljtfreitas.julian.http.HTTPResponseException
import com.github.ljtfreitas.julian.http.HTTPResponseFailure.empty
import com.github.ljtfreitas.julian.http.HTTPStatus
import com.github.ljtfreitas.julian.http.HTTPStatusCode
import com.github.ljtfreitas.julian.k.failure
import com.github.ljtfreitas.julian.k.http
import com.github.ljtfreitas.julian.k.proxy
import com.github.ljtfreitas.julian.k.`when`
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

typealias Id = Int

@Path("/pets")
interface PetsApi {

    @POST
    suspend fun create(@JsonContent pet: NewPet): Either<HTTPResponseException, Pet>

    @POST
    suspend fun createOr(@JsonContent pet: NewPet): Either<Problems, Pet>

    @POST
    fun createAsEffect(@JsonContent pet: NewPet): Effect<HTTPResponseException, Pet>

    @GET("/{id}")
    suspend fun read(@Path id: Id): Option<Pet>

    @PUT("/{id}")
    suspend fun update(@Path id: Id, @JsonContent pet: NewPet): Either<HTTPResponseException, HTTPResponse<Pet>>

    @DELETE("/{id}")
    suspend fun delete(@Path id: Id): Either<HTTPResponseException, HTTPStatus>
}

@Serializable
data class NewPet(val name: String, val age: Int)

@Serializable
data class Pet(val id: Id, val name: String, val age: Int)

@Serializable
data class Problems(val problems: List<Problem>)

@Serializable
data class Problem(val field: String, val message: String)

@DelicateCoroutinesApi
fun main() {
    val petsApi: PetsApi = proxy(endpoint = "http://localhost:7070") {
        http {
            failure {
                `when`(HTTPStatusCode.NOT_FOUND to empty())
            }
        }
    }

    runBlocking {
        println("running suspended functions...")

        val newPet: Either<HTTPResponseException, Pet> = petsApi.create(pet = NewPet(name = "Fiona", age = 1))

        newPet.tap { pet -> println("a new pet was created: $pet...") }
            .tapLeft { e -> e.printStackTrace() }

        newPet.traverseOption { pet -> petsApi.read(id = pet.id) }
            .tap { pet -> println("the pet was found: $pet...") }

        petsApi.read(id = 9999)
            .tap { pet -> println("the pet was found: $pet...") }
            .tapNone { println("the pet was not found...") }

        newPet.flatMap { pet -> petsApi.update(id = pet.id, pet = NewPet(name = pet.name, age = pet.age + 1)) }
            .tap { response ->
                response.onSuccess { status, _, pet ->
                    println("the pet was updated: $pet...and the HTTP status was $status.")

                }.onFailure { e -> e.printStackTrace() }

            }.tapLeft { e -> e.printStackTrace() }

        newPet.flatMap { pet -> petsApi.delete(id = pet.id).map { status -> status to pet } }
            .tap { (status, pet) ->
                println("the pet ${pet.id} was deleted...and the HTTP status was $status.")

            }.tapLeft { e -> e.printStackTrace() }

        delay(1000)
    }

    runBlocking {
        println("running effect functions...")

        val newPetAsEffect: Effect<HTTPResponseException, Pet> = petsApi.createAsEffect(pet = NewPet(name = "Hugo", age = 2))

        val newPet = newPetAsEffect.toEither()

        newPet.tap { pet -> println("a new pet was created: $pet...") }
            .tapLeft { e -> e.printStackTrace() }
    }

    runBlocking {
        // now, we are going to see how we can recover from a failure response using an Either<MyErrorType, MySuccessType>
        // in case we send an invalid pet, we will receive a Bad Request response with a json explaining the problems
        petsApi.createOr(NewPet(name = "", age = 0))
            .tap { pet -> println("a new pet was created: $pet...") }
            .tapLeft { problems: Problems -> println("there is some problems with our pet: $problems...") }
    }

}