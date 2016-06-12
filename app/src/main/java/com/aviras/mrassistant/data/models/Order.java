package com.aviras.mrassistant.data.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Represent order
 * <p/>
 * Created by ashish on 8/6/16.
 */
public class Order extends RealmObject {

    @PrimaryKey
    private int id;

    private Doctor doctor;

    private RealmList<OrderItem> items = new RealmList<>();

    private long createdDate;
    private long expectedDeliveryDate;
    private long actualDeliveryDate;

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
}
