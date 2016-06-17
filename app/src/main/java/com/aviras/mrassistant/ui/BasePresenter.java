package com.aviras.mrassistant.ui;

import android.util.Log;

import io.realm.Realm;

/**
 * Base for all presenter. Holds reference to common data
 * <p/>
 * Created by ashish on 14/6/16.
 */
public abstract class BasePresenter implements Presenter {

    private final String LOG_TAG = this.getClass().getSimpleName();
    protected Realm mRealm;

    public void init(Realm realm) {
        Log.d(LOG_TAG, "init - realm: " + realm);
        mRealm = realm;
    }

    public void removeAllChangeListeners() {
        Log.d(LOG_TAG, "removeAllChangeListeners");
        mRealm.removeAllChangeListeners();
    }
}
