package com.aviras.mrassistant.data.models;

import io.realm.RealmObject;

/**
 * Represent order item
 * <p/>
 * Created by ashish on 8/6/16.
 */
public class OrderItem extends RealmObject {

    private Medicine medicine;

    private Unit unit;

    private float quantity;

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }
}
