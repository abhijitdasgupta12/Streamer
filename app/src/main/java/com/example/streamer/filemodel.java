package com.example.streamer;

public class filemodel
{
    String date,time,user_id,user_image,user_name,video_title, video_url;

    public filemodel() {
    }

    public filemodel(String date, String time, String user_id, String user_image, String user_name, String video_title, String video_url) {
        this.date = date;
        this.time = time;
        this.user_id = user_id;
        this.user_image = user_image;
        this.user_name = user_name;
        this.video_title = video_title;
        this.video_url = video_url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getVideo_title() {
        return video_title;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }
}
