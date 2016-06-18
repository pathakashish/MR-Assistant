package com.aviras.mrassistant.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.aviras.mrassistant.ui.utils.ParcelableUtil;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Represent medicine
 * <p/>
 * Created by ashish on 8/6/16.
 */
public class Medicine extends RealmObject implements Parcelable {

    @PrimaryKey
    private int id;

    private String name;

    private String description;

    private RealmList<Unit> supportedUnits = new RealmList<>();

    public Medicine() {

    }

    protected Medicine(Parcel in) {
        id = ParcelableUtil.readInt(in);
        name = ParcelableUtil.readString(in);
        description = ParcelableUtil.readString(in);
        List<Parcelable> listFromParcel = ParcelableUtil.readParcelableList(in);
        if(null != listFromParcel) {
            for (Parcelable item : listFromParcel) {
                supportedUnits.add((Unit) item);
            }
        }
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RealmList<Unit> getSupportedUnits() {
        return supportedUnits;
    }

    public void setSupportedUnits(RealmList<Unit> supportedUnits) {
        this.supportedUnits = supportedUnits;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelableUtil.write(dest, id);
        ParcelableUtil.write(dest, name);
        ParcelableUtil.write(dest, description);
        ParcelableUtil.write(dest, supportedUnits, 0);
    }

    @Ignore
    public static final Creator<Medicine> CREATOR = new Creator<Medicine>() {
        @Override
        public Medicine createFromParcel(Parcel in) {
            return new Medicine(in);
        }

        @Override
        public Medicine[] newArray(int size) {
            return new Medicine[size];
        }
    };
}
