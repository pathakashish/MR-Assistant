package com.aviras.mrassistant.models;

import io.realm.annotations.PrimaryKey;

/**
 * Created by ashish on 8/6/16.
 */
public class Doctor {

    @PrimaryKey
    private int id;

    private String name;

    private String address;

    private String notes;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
