package com.aviras.mrassistant.models;

import java.util.ArrayList;
import java.util.List;

import io.realm.annotations.PrimaryKey;

/**
 * Represent order
 * <p/>
 * Created by ashish on 8/6/16.
 */
public class Order {

    @PrimaryKey
    private int id;

    private Doctor doctor;

    private List<Medicine> medicines = new ArrayList<>();

    private long createdDate;
    private long expectedDeliveryDate;
    private long actualDeliveryDate;
}
