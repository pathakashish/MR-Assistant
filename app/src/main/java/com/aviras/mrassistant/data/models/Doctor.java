package com.aviras.mrassistant.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.aviras.mrassistant.ui.utils.ParcelableUtil;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Represent doctor
 * <p/>
 * Created by ashish on 8/6/16.
 */
public class Doctor extends RealmObject implements Parcelable {

    @PrimaryKey
    private int id;

    private String name;

    private String contactNumber;

    private String address;

    private String notes;

    public Doctor() {

    }

    protected Doctor(Parcel in) {
        id = ParcelableUtil.readInt(in);
        name = ParcelableUtil.readString(in);
        contactNumber = ParcelableUtil.readString(in);
        address = ParcelableUtil.readString(in);
        notes = ParcelableUtil.readString(in);
    }

    public Doctor(Doctor doctor) {
        if(null == doctor) {
            return;
        }
        id = doctor.getId();
        name = doctor.getName();
        contactNumber = doctor.getContactNumber();
        address = doctor.getAddress();
        notes = doctor.getNotes();
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelableUtil.write(dest, id);
        ParcelableUtil.write(dest, name);
        ParcelableUtil.write(dest, contactNumber);
        ParcelableUtil.write(dest, address);
        ParcelableUtil.write(dest, notes);
    }

    @Ignore
    public static final Creator<Doctor> CREATOR = new Creator<Doctor>() {
        @Override
        public Doctor createFromParcel(Parcel in) {
            return new Doctor(in);
        }

        @Override
        public Doctor[] newArray(int size) {
            return new Doctor[size];
        }
    };
}
