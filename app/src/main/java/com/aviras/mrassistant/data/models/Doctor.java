package com.aviras.mrassistant.data.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Represent doctor
 * <p/>
 * Created by ashish on 8/6/16.
 */
public class Doctor extends RealmObject {

    @PrimaryKey
    private int id;

    private String name;

    private String contactNumber;

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

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
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
