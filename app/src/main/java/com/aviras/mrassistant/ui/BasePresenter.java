package com.aviras.mrassistant.ui;

import io.realm.Realm;

/**
 * Created by ashish on 14/6/16.
 */
public abstract class BasePresenter implements Presenter {

    protected Realm mRealm;

    @Override
    public void closeDatabase() {
        mRealm.removeAllChangeListeners();
        mRealm.close();
    }

    @Override
    public void openDatabase() {
        mRealm = Realm.getDefaultInstance();
    }
}
