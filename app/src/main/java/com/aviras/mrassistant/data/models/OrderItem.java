package com.aviras.mrassistant.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.aviras.mrassistant.ui.utils.ParcelableUtil;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Represent order item
 * <p/>
 * Created by ashish on 8/6/16.
 */
public class OrderItem extends RealmObject implements Parcelable {

    private Medicine medicine;

    private SupportedUnit unit;

    private float quantity;

    private String note;

    public OrderItem() {

    }

    protected OrderItem(Parcel in) {
        medicine = ParcelableUtil.readParcelable(in, Medicine.class.getClassLoader());
        unit = ParcelableUtil.readParcelable(in, Unit.class.getClassLoader());
        quantity = ParcelableUtil.readFloat(in);
        note = ParcelableUtil.readString(in);
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public SupportedUnit getUnit() {
        return unit;
    }

    public void setUnit(SupportedUnit unit) {
        this.unit = unit;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelableUtil.write(dest, medicine, flags);
        ParcelableUtil.write(dest, unit, flags);
        ParcelableUtil.write(dest, quantity);
        ParcelableUtil.write(dest, note);
    }

    @Ignore
    public static final Creator<OrderItem> CREATOR = new Creator<OrderItem>() {
        @Override
        public OrderItem createFromParcel(Parcel in) {
            return new OrderItem(in);
        }

        @Override
        public OrderItem[] newArray(int size) {
            return new OrderItem[size];
        }
    };
}
