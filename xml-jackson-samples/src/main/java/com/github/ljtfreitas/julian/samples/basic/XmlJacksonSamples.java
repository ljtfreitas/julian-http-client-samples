package com.github.ljtfreitas.julian.samples.basic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.contract.GET;
import com.github.ljtfreitas.julian.contract.POST;
import com.github.ljtfreitas.julian.contract.XmlContent;

import java.util.Collection;

class XmlJacksonSamples {

    public static void main(String[] args) throws InterruptedException {
        // Jackson codecs will be automatically registered

        HTTPBin httpBin = new ProxyBuilder()
                .build(HTTPBin.class, "https://httpbin.org");

        System.out.println("POST: " + httpBin.postAsXml(new Pet("Hugo", 2)));
        System.out.println("#############");

        System.out.println("GET: " + httpBin.readAsXml());
        System.out.println("#############");
    }

    interface HTTPBin {

        @POST("/post")
        String postAsXml(@XmlContent Pet pet);

        @GET("/xml")
        Slideshow readAsXml();
    }

    record Pet(String name, int age) {}

    record Slideshow(String title, String date, String author,
                     @JacksonXmlElementWrapper(useWrapping = false)
                     @JacksonXmlProperty(localName = "slide")
                     Collection<Slide> slides) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Slide(String type, String title) {}

}
