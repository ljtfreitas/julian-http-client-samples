package com.github.ljtfreitas.julian.samples.kotlin

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
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

typealias Id = Int

@Path("/pets")
interface PetsApi {

    @POST
    suspend fun create(@JsonContent pet: NewPet): Pet

    @GET("/{id}")
    suspend fun read(@Path id: Id): Pet?

    @PUT("/{id}")
    suspend fun update(@Path id: Id, @JsonContent pet: NewPet): HTTPResponse<Pet>

    @DELETE("/{id}")
    suspend fun delete(@Path id: Id): HTTPStatus
}

@Serializable
data class NewPet(val name: String, val age: Int)

@Serializable
data class Pet(val id: Id, val name: String, val age: Int)

fun main() {
    val petsApi: PetsApi = proxy(endpoint = "http://localhost:7070") {
        http {
            failure {
                `when`(HTTPStatusCode.NOT_FOUND to empty())
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
}