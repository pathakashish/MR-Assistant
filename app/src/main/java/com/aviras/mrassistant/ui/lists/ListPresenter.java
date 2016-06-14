package com.aviras.mrassistant.ui.lists;

import com.aviras.mrassistant.ui.Presenter;

import io.realm.RealmObject;

/**
 * Presenter interface for showing list
 * <p/>
 * Created by ashish on 14/6/16.
 */
public interface ListPresenter<T extends RealmObject> extends Presenter {

    /**
     * Loads list of {@link RealmObject}s
     */
    void load();

    /**
     * Set the list view. This view will get all view related callbacks
     *
     * @param listView
     */
    void setView(ListView listView);
}
