package com.github.ljtfreitas.julian.samples

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.delete
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.path
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.apibuilder.ApiBuilder.put
import io.javalin.core.util.FileUtil
import io.javalin.http.HttpCode
import io.javalin.http.UploadedFile
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createTempFile

typealias PetId = Int
typealias PhotoId = Int

fun main() {
    val pets = mutableMapOf<PetId, Pet>()

    val app = Javalin.create().start(7070)

    app.routes {
        path("/pets") {
            get {
                it.json(pets.values.mapIndexed { index, pet -> (index + 1 to pet).asMap() })
            }
            post {
                val newPet = it.bodyAsClass<NewPet>()

                when (val pet = newPet.pet) {
                    null -> newPet.problems.also { problems -> it.status(HttpCode.BAD_REQUEST).json(mapOf("problems" to problems)) }
                    else -> pets.put(pet).also { p -> it.status(HttpCode.CREATED).json(p.asMap()) }
                }

            }
            path("/{id}") {
                get {
                    val id = it.pathParam("id").toInt()

                    when (val pet = pets[id]) {
                        null -> it.status(HttpCode.NOT_FOUND)
                        else -> it.json((id to pet).asMap())
                    }
                }
                put {
                    val id = it.pathParam("id").toInt()
                    val newPet = it.bodyAsClass<NewPet>()

                    when (val pet = newPet.pet) {
                        null -> newPet.problems.also { problems -> it.status(HttpCode.BAD_REQUEST).json(mapOf("problems" to problems)) }
                        else -> pet.also { p ->
                            val statusCode = if (pets.containsKey(id)) HttpCode.OK else HttpCode.CREATED
                            pets[id] = p
                            it.status(statusCode).json((id to p).asMap())
                        }
                    }
                }
                delete {
                    val id = it.pathParam("id").toInt()
                    pets.remove(id)
                }
                path("/pictures") {
                    post { c ->
                        val id = c.pathParam("id").toInt()

                        val upload: (PetId, UploadedFile?) -> Path? = { id, file ->
                            file?.run {
                                val dest = createTempFile(prefix = id.toString(), suffix = filename)
                                content.use {
                                    FileUtil.streamToFile(inputStream = it, path = dest.absolutePathString())
                                    dest
                                }
                            }
                        }

                        when (val pet = pets[id]) {
                            null -> c.status(HttpCode.NOT_FOUND)
                            else -> upload(id, c.uploadedFile("picture"))
                                ?.also { pathToPhoto -> pet.pictures.add(pathToPhoto) }
                                ?.also { pathToPhoto -> c.status(HttpCode.CREATED)
                                    .json(Picture(petId = id, pictureId = pet.pictures.size, path = pathToPhoto.absolutePathString()))
                                }
                        }
                    }
                    get("/{pictureId}") {
                        val petId = it.pathParam("id").toInt()
                        val pictureId = it.pathParam("pictureId").toInt()

                        when (val pet = pets[petId]) {
                            null -> it.status(HttpCode.NOT_FOUND)
                            else -> it.result(Files.readAllBytes(pet.pictures[pictureId - 1]))
                        }
                    }
                }
            }
        }
    }
}

fun Pair<PetId, Pet>.asMap(): Map<String, Any> {
    val (id, pet) = this
    return mapOf("id" to id, "name" to pet.name, "age" to pet.age)
}

fun MutableMap<PetId, Pet>.put(pet: Pet): Pair<PetId, Pet> {
    val id = (keys.maxOrNull() ?: 0).inc()
    put(id, pet)
    return id to pet
}

data class Problem(val field: String, val message: String)

data class NewPet(val name: String?, val age: Int?) {

    val pet: Pet? = if (name == null || age == null || age <= 0) null else Pet(name = name, age = age)

    val problems = mutableListOf<Problem>().apply {
        if (name.isNullOrEmpty())
            add(Problem(field = "name", message = "name is required"))
        if (age == null)
            add(Problem(field = "age", message = "age is required"))
        else if (age <= 0)
            add(Problem(field = "age", message = "age must be greather than 0"))

    }.toList()
}

data class Pet(val name: String, val age: Int) {

    val pictures: MutableList<Path> = mutableListOf()
}

data class Picture(val petId: PetId, val pictureId: PhotoId, val path: String)