package com.github.ljtfreitas.julian.samples.basic;

import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.contract.MultipartFormData;
import com.github.ljtfreitas.julian.contract.POST;
import com.github.ljtfreitas.julian.multipart.MultipartForm;
import com.github.ljtfreitas.julian.multipart.MultipartForm.Part;

public class Upload {

    public static void main(String[] args) {
        UploadHTTPBin httpBin = new ProxyBuilder()
                .build(UploadHTTPBin.class, "https://eohojx85kzlef7r.m.pipedream.net");

        MultipartForm form = new MultipartForm()
                .join(Part.create("name", "cat"))
                .join(Part.create("file", Upload.class.getResourceAsStream("/cat.png"), "cat.png"));

        System.out.println("Upload: " + httpBin.upload(form));
        System.out.println("#############");
    }

    public interface UploadHTTPBin {

        @POST("/post")
        String upload(@MultipartFormData MultipartForm form);
    }

}
