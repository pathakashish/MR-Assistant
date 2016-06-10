package com.aviras.mrassistant.data.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Unit for medicines
 * <p/>
 * Created by ashish on 8/6/16.
 */
public class Unit extends RealmObject {

    @PrimaryKey
    private int id;

    private CharSequence name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CharSequence getName() {
        return name;
    }

    public void setName(CharSequence name) {
        this.name = name;
    }
}
