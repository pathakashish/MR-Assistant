package com.aviras.mrassistant.ui.lists;

import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Implement this interface to show list
 * <p/>
 * Created by ashish on 11/6/16.
 */
public interface ListView<T extends RealmObject> {
    void setItems(RealmResults<T> items);
}
