package com.ilma.testing.ilmaapp.Models;

/**
 * Created by fazal on 8/24/2017.
 */

public class Image {
    String imageID;
    String base64Img;

    public Image() {
    }

    public Image(String imageID, String base64Img) {
        this.imageID = imageID;
        this.base64Img = base64Img;
    }

    public String getImageID() {
        return imageID;
    }

    public String getBase64Img() {
        return base64Img;
    }
}
