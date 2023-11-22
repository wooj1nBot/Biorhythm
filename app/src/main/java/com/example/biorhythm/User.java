package com.example.biorhythm;

import android.net.Uri;

import java.io.Serializable;
import java.util.Calendar;

public class User implements Serializable {
    String name;
    Calendar birthday;
    int color;
    String image;


    public User(){}

    public User(String name, Calendar birthday, int color, String image){
        this.name = name;
        this.birthday = birthday;
        this.color = color;
        this.image = image;
    }

    public void setBirthday(Calendar birthday) {
        this.birthday = birthday;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Calendar getBirthday() {
        return birthday;
    }

    public int getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public Uri getImage() {
        if(image == null) return null;
        return Uri.parse(image);
    }
}
