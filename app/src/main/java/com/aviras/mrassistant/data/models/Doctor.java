package com.aviras.mrassistant.data.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Represent doctor
 * <p/>
 * Created by ashish on 8/6/16.
 */
public class Doctor extends RealmObject{

    @PrimaryKey
    private int id;

    private CharSequence name;

    private CharSequence address;

    private CharSequence notes;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CharSequence getName() {
        return name;
    }

    public void setName(CharSequence name) {
        this.name = name;
    }

    public CharSequence getAddress() {
        return address;
    }

    public void setAddress(CharSequence address) {
        this.address = address;
    }

    public CharSequence getNotes() {
        return notes;
    }

    public void setNotes(CharSequence notes) {
        this.notes = notes;
    }
}
