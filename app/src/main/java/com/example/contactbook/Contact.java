package com.example.contactbook;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.Comparator;

/**
 * Created by me on 01.06.2017.
 */

public class Contact implements Comparator<Contact>, Comparable<Contact> {

    private long id;
    private String name;
    private String lastName;
    private String number;
    private String email;
    private String photo;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return name + " " + lastName;
    }


    @Override
    public int compare(Contact o1, Contact o2) {
        return o1.getName().compareTo(o2.getName());
    }

    @Override
    public int compareTo(@NonNull Contact o) {
        return (this.getName()).compareTo(o.getName());
    }
}
