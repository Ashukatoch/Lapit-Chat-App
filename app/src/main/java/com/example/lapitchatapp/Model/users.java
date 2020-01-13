package com.example.lapitchatapp.Model;

import java.io.Serializable;

public class users implements Serializable
{
    public String name,status,image,thumbimage;
    public users(String name, String status, String image, String thumbimage) {
        this.name = name;
        this.status = status;
        this.image = image;
        this.thumbimage = thumbimage;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumbimage() {
        return thumbimage;
    }

    public void setThumbimage(String thumbimage) {
        this.thumbimage = thumbimage;
    }

    users()
        {}


}
