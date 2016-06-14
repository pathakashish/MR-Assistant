package com.aviras.mrassistant.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.aviras.mrassistant.ui.utils.ParcelableUtil;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Unit for medicines
 * <p/>
 * Created by ashish on 8/6/16.
 */
public class Unit extends RealmObject implements Parcelable {

    @PrimaryKey
    private int id;

    private String name;

    public Unit() {

    }

    protected Unit(Parcel in) {
        id = ParcelableUtil.readInt(in);
        name = ParcelableUtil.readString(in);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelableUtil.write(dest, id);
        ParcelableUtil.write(dest, name);
    }

    @Ignore
    public static final Creator<Unit> CREATOR = new Creator<Unit>() {
        @Override
        public Unit createFromParcel(Parcel in) {
            return new Unit(in);
        }

        @Override
        public Unit[] newArray(int size) {
            return new Unit[size];
        }
    };
}
