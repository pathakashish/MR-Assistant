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
 * Represent order
 * <p/>
 * Created by ashish on 8/6/16.
 */
public class Order extends RealmObject implements Parcelable {

    @PrimaryKey
    private int id;

    private Doctor doctor;

    private RealmList<OrderItem> items = new RealmList<>();

    private String specialRequest;

    private long createdDate;
    private long expectedDeliveryDate;
    private long actualDeliveryDate;

    public Order() {

    }

    protected Order(Parcel in) {
        id = ParcelableUtil.readInt(in);
        doctor = ParcelableUtil.readParcelable(in);
        createdDate = ParcelableUtil.readLong(in);
        expectedDeliveryDate = ParcelableUtil.readLong(in);
        actualDeliveryDate = ParcelableUtil.readLong(in);
        List<Parcelable> listFromParcel = ParcelableUtil.readParcelableList(in);
        if (null != listFromParcel) {
            for (Parcelable item : listFromParcel) {
                items.add((OrderItem) item);
            }
        }
        specialRequest = ParcelableUtil.readString(in);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public RealmList<OrderItem> getItems() {
        return items;
    }

    public void setItems(RealmList<OrderItem> items) {
        this.items = items;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public long getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(long expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public long getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public void setActualDeliveryDate(long actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public String getSpecialRequest() {
        return specialRequest;
    }

    public void setSpecialRequest(String specialRequest) {
        this.specialRequest = specialRequest;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelableUtil.write(dest, id);
        ParcelableUtil.write(dest, doctor, flags);
        ParcelableUtil.write(dest, createdDate);
        ParcelableUtil.write(dest, expectedDeliveryDate);
        ParcelableUtil.write(dest, actualDeliveryDate);
        ParcelableUtil.write(dest, items, 0);
        ParcelableUtil.write(dest, specialRequest);
    }

    @Ignore
    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
}
