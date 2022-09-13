package com.github.ljtfreitas.julian.samples.jsonb;

import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.contract.GET;
import com.github.ljtfreitas.julian.contract.JsonContent;
import com.github.ljtfreitas.julian.contract.POST;
import com.github.ljtfreitas.julian.contract.Path;

class JsonBSamples {

    public static void main(String[] args) throws InterruptedException {
        // jsonb codecs will be automatically registered

        PetsApi petsApi = new ProxyBuilder()
                .build(PetsApi.class, "http://localhost:7070");

        Pet pet = petsApi.create(new NewPet("Hugo", 2));

        System.out.println("POST: " + pet);
        System.out.println("#############");

        System.out.println("GET: " + petsApi.read(pet.id));
        System.out.println("#############");
    }

    @Path("/pets")
    interface PetsApi {

        @POST
        Pet create(@JsonContent NewPet pet);

        @GET("/{id}")
        Pet read(@Path int id);
    }

    public static class NewPet {

        public final String name;
        public final int age;

        NewPet(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    public static class Pet {

        public int id;
        public String name;
        public int age;

        @Override
        public String toString() {
            return "Pet{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }
}
