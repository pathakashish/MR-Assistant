package com.aviras.mrassistant.models;

import java.util.ArrayList;
import java.util.List;

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

    private List<Unit> supportedUnits = new ArrayList<>();

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

    public List<Unit> getSupportedUnits() {
        return supportedUnits;
    }

    public void setSupportedUnits(List<Unit> supportedUnits) {
        this.supportedUnits = supportedUnits;
    }
}
