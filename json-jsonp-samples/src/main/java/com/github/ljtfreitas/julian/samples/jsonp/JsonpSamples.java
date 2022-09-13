package com.github.ljtfreitas.julian.samples.jsonp;

import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.contract.GET;
import com.github.ljtfreitas.julian.contract.JsonContent;
import com.github.ljtfreitas.julian.contract.POST;
import com.github.ljtfreitas.julian.contract.Path;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

class JsonpSamples {

    public static void main(String[] args) throws InterruptedException {
        // jsonp codecs will be automatically registered

        PetsApi petsApi = new ProxyBuilder()
                .build(PetsApi.class, "http://localhost:7070");

        JsonObject newPetAsJson = Json.createObjectBuilder()
                .add("name", "Puka")
                .add("age", 2)
                .build();

        JsonObject petAsJson = petsApi.create(newPetAsJson);

        System.out.println("POST: " + petAsJson);
        System.out.println("#############");

        System.out.println("GET: " + petsApi.read(petAsJson.getInt("id")));
        System.out.println("#############");

        System.out.println("GET: " + petsApi.readAll());
        System.out.println("#############");
    }

    @Path("/pets")
    interface PetsApi {

        @POST
        JsonObject create(@JsonContent JsonObject newPetAsJson);

        @GET("/{id}")
        JsonObject read(@Path int id);

        @GET
        JsonArray readAll();
    }
}
