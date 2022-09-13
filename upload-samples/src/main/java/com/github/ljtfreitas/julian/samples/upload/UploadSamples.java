package com.github.ljtfreitas.julian.samples.upload;

import com.github.ljtfreitas.julian.Attempt;
import com.github.ljtfreitas.julian.Promise;
import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.contract.GET;
import com.github.ljtfreitas.julian.contract.JsonContent;
import com.github.ljtfreitas.julian.contract.POST;
import com.github.ljtfreitas.julian.contract.Path;
import com.github.ljtfreitas.julian.contract.UploadContent;
import com.github.ljtfreitas.julian.http.Download;
import com.github.ljtfreitas.julian.http.MediaType;
import com.github.ljtfreitas.julian.http.Upload;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.function.Function;

class UploadSamples {

    public static void main(String[] args) {
        PetsApi petsApi = new ProxyBuilder()
                .build(PetsApi.class, "http://localhost:7070");

        Function<Pet, Promise<Photo>> upload = (pet -> petsApi.upload(pet.id,
                Upload.ofInputStream(UploadSamples.class.getResourceAsStream("/photo.jpg"), "photo", "photo.jpg", MediaType.IMAGE_JPEG)));

        Function<Photo, Promise<java.nio.file.Path>> download = (photo -> petsApi.download(photo.petId, photo.photoId)
                .writeTo(Attempt.run(() -> Files.createTempFile("photo-pet", photo.petId + "-" + photo.photoId + ".jpg")).prop(), StandardOpenOption.WRITE));

        petsApi.create(new NewPet("Mel", 3))
            .onSuccess(pet -> System.out.println("a new pet was created: " + pet + "..."))
            .bind(upload)
            .onSuccess(photo -> System.out.println("a pet photo was uploaded: " + photo + "..."))
            .bind(download)
            .onSuccess(path -> System.out.println("a pet photo was download: " + path + "..."))
            .join()
            .onFailure(Throwable::printStackTrace);
    }

    @Path("/pets")
    interface PetsApi {

        @POST
        Promise<Pet> create(@JsonContent NewPet pet);

        @POST("/{petId}/photos")
        Promise<Photo> upload(@Path int petId, @UploadContent Upload<InputStream> content);

        @GET("/{petId}/photos/{photoId}")
        Download download(@Path int petId, @Path int photoId);
    }

    record NewPet(String name, int age) {}

    record Pet(int id, String name, int age) {}

    record Photo(int petId, int photoId) {}

}
