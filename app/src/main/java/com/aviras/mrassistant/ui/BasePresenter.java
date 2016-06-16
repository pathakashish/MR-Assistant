package com.aviras.mrassistant.ui;

import android.util.Log;

import io.realm.Realm;

/**
 * Created by ashish on 14/6/16.
 */
public abstract class BasePresenter implements Presenter {

    private static final String LOG_TAG = BasePresenter.class.getSimpleName();
    protected Realm mRealm;

    @Override
    public void closeDatabase() {
        Log.v(LOG_TAG, "closeDatabase");
        mRealm.removeAllChangeListeners();
        mRealm.close();
        onDatabaseClosed();
    }

    protected void onDatabaseClosed() {

    }

    @Override
    public void openDatabase() {
        Log.v(LOG_TAG, "openDatabase");
        mRealm = Realm.getDefaultInstance();
        onDatabaseOpened();
    }

    protected void onDatabaseOpened() {

    }
}
