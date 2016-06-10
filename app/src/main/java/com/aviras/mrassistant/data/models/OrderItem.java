package com.aviras.mrassistant.data.models;

import io.realm.RealmObject;

/**
 * Represent order item
 * <p/>
 * Created by ashish on 8/6/16.
 */
public class OrderItem extends RealmObject {

    private int medicineId;

    private String medicineName;

    private Unit unit;

    private float quantity;

    public int getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(int medicineId) {
        this.medicineId = medicineId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
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
