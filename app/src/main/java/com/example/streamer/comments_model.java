package com.example.streamer;

public class comments_model
{
    String date,message,time,user_id,user_image,user_name;

    public comments_model() {
    }

    public comments_model(String date, String message, String time, String user_id, String user_image, String user_name) {
        this.date = date;
        this.message = message;
        this.time = time;
        this.user_id = user_id;
        this.user_image = user_image;
        this.user_name = user_name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
