package com.aviras.mrassistant.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Represent medicine
 * <p/>
 * Created by ashish on 8/6/16.
 */
public class Medicine extends RealmObject {

    @PrimaryKey
    private int id;

    private String name;

    private RealmList<Unit> supportedUnits = new RealmList<>();

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

    public RealmList<Unit> getSupportedUnits() {
        return supportedUnits;
    }

    public void setSupportedUnits(RealmList<Unit> supportedUnits) {
        this.supportedUnits = supportedUnits;
    }
}
