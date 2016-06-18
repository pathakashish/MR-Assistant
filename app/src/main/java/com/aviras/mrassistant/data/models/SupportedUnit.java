package com.aviras.mrassistant.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.aviras.mrassistant.ui.utils.ParcelableUtil;

import io.realm.RealmObject;

/**
 * Will more info about unit when selected for medicine
 * <p/>
 * Created by ashish on 18/6/16.
 */
public class SupportedUnit extends RealmObject implements Parcelable {

    private Unit unit;

    private float unitPrice;

    private boolean isDefault;

    public SupportedUnit() {

    }

    protected SupportedUnit(Parcel in) {
        unit = ParcelableUtil.readParcelable(in);
        unitPrice = ParcelableUtil.readFloat(in);
        isDefault = ParcelableUtil.readBoolean(in);
    }

    public SupportedUnit(SupportedUnit supportedUnit) {
        this.unit = supportedUnit.getUnit();
        this.unitPrice = supportedUnit.getUnitPrice();
        this.isDefault = supportedUnit.isDefault();
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public float getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(float unitPrice) {
        this.unitPrice = unitPrice;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public static final Creator<SupportedUnit> CREATOR = new Creator<SupportedUnit>() {
        @Override
        public SupportedUnit createFromParcel(Parcel in) {
            return new SupportedUnit(in);
        }

        @Override
        public SupportedUnit[] newArray(int size) {
            return new SupportedUnit[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelableUtil.write(dest, unit, flags);
        ParcelableUtil.write(dest, unitPrice);
        ParcelableUtil.write(dest, isDefault);
    }
}
