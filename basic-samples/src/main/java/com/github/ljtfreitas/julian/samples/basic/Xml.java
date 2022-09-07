package com.github.ljtfreitas.julian.samples.basic;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.contract.GET;
import com.github.ljtfreitas.julian.contract.POST;
import com.github.ljtfreitas.julian.contract.XmlContent;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

public class Xml {

    public static void main(String[] args) {
        XmlHTTPBin httpBin = new ProxyBuilder().build(XmlHTTPBin.class, "https://httpbin.org");

        System.out.println("GET: " + httpBin.xml());
        System.out.println("#############");

        System.out.println("POST: " + httpBin.xml(new Cat("Hugo", 2)).toPrettyString());
        System.out.println("#############");
    }

    public interface XmlHTTPBin {

        @POST("/post")
        JsonNode xml(@XmlContent Cat body);

        @GET("/xml")
        Slideshow xml();
    }

    @XmlRootElement
    public static class Slideshow {

        @XmlAttribute
        String author;

        @XmlAttribute
        String date;

        @XmlAttribute
        String title;

        @Override
        public String toString() {
            return "Slideshow[" +
                    "author='" + author + '\'' +
                    ", date='" + date + '\'' +
                    ", title='" + title + '\'' +
                    "]";
        }
    }

    @XmlRootElement
    static class Cat {

        @XmlAttribute
        String name;

        @XmlAttribute
        int age;

        Cat() {}

        Cat(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

}
