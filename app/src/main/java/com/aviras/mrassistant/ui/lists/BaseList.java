package com.aviras.mrassistant.ui.lists;

import com.aviras.mrassistant.ui.BasePresenter;

import io.realm.RealmChangeListener;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Base list presenter for list
 * <p/>
 * Created by ashish on 14/6/16.
 */
public abstract class BaseList<T extends RealmObject> extends BasePresenter implements ListPresenter<T>, RealmChangeListener<RealmResults<T>> {

    protected ListView mListView;
    private Class<T> mClassType;

    public BaseList(Class<T> classType) {
        mClassType = classType;
    }

    @Override
    public void load() {
        RealmResults<T> list = mRealm.where(mClassType).findAllAsync();
        list.addChangeListener(this);
    }

    @Override
    public void setView(ListView listView) {
        mListView = listView;
    }

    @Override
    public void onChange(RealmResults<T> element) {
        if (null != mListView) {
            mListView.setItems(element);
        }
    }
}
