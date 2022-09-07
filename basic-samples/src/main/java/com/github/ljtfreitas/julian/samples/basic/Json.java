package com.github.ljtfreitas.julian.samples.basic;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.contract.GET;
import com.github.ljtfreitas.julian.contract.JsonContent;
import com.github.ljtfreitas.julian.contract.POST;
import com.github.ljtfreitas.julian.http.HTTPResponse;
import com.github.ljtfreitas.julian.http.HTTPStatus;

import java.util.Collection;

public class Json {

    public static void main(String[] args) {
        JsonHTTPBin httpBin = new ProxyBuilder().build(JsonHTTPBin.class, "https://httpbin.org");

        System.out.println("GET: " + httpBin.json());
        System.out.println("#############");

        System.out.println("POST: " + httpBin.json(new Cat("Hugo", 2)).toPrettyString());
        System.out.println("#############");
    }

    public interface JsonHTTPBin {

        @POST("/post")
        JsonNode json(@JsonContent Cat body);

        @GET("/json")
        Sample json();
    }

    public record Sample(Slideshow slideshow) {}

    public record Slideshow(String author, String date, Collection<Slide> slides, String title) {}

    public record Slide(String title, String type, Collection<String> items) {}

    public record Cat(String name, int age) {}
}
