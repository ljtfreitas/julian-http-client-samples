package com.github.ljtfreitas.julian.samples.basic;

import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.contract.GET;
import com.github.ljtfreitas.julian.contract.POST;
import com.github.ljtfreitas.julian.contract.XmlContent;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.Collection;

class XmlJaxbSamples {

    public static void main(String[] args) throws InterruptedException {
        // jaxb codecs will be automatically registered

        HTTPBin httpBin = new ProxyBuilder()
                .build(HTTPBin.class, "https://httpbin.org");

//        System.out.println("POST: " + httpBin.postAsXml(new Pet("Hugo", 2)));
//        System.out.println("#############");

        System.out.println("GET: " + httpBin.readAsXml());
        System.out.println("#############");
    }

    interface HTTPBin {

        @POST("/post")
        String postAsXml(@XmlContent Pet pet);

        @GET("/xml")
        Slideshow readAsXml();
    }

    @XmlRootElement
    static class Slideshow {

        @XmlAttribute
        public String title;

        @XmlAttribute
        public String date;

        @XmlAttribute
        public String author;

        @XmlElement(name = "slide")
        public Collection<Slide> slides;

        @Override
        public String toString() {
            return "Slideshow{" +
                    "title='" + title + '\'' +
                    ", date='" + date + '\'' +
                    ", author='" + author + '\'' +
                    ", slides='" + slides + '\'' +
                    '}';
        }
    }

    static class Slide {

        @XmlAttribute
        public String type;

        @XmlElement
        public String title;

        @XmlElement(name = "item")
        public Collection<String> items = new ArrayList<>();

        @Override
        public String toString() {
            return "Slide{" +
                    "type='" + type + '\'' +
                    ", title='" + title + '\'' +
                    ", items='" + items + '\'' +
                    '}';
        }
    }

    @XmlRootElement
    static class Pet {

        public String name;
        public int age;

        Pet() {}

        Pet(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }


}
