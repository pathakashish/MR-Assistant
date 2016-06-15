package com.aviras.mrassistant.ui.medicines;

import android.content.Context;
import android.content.Intent;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.Medicine;
import com.aviras.mrassistant.ui.BasePresenter;
import com.aviras.mrassistant.ui.Presenter;
import com.aviras.mrassistant.ui.editors.EditorActivity;
import com.aviras.mrassistant.ui.lists.ListPresenter;
import com.aviras.mrassistant.ui.lists.ListView;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Presenter for {@link Medicine}s list
 * <p/>
 * Created by ashish on 14/6/16.
 */
public class MedicinesList extends BasePresenter implements ListPresenter<Medicine>, RealmChangeListener<RealmResults<Medicine>> {

    private static final MedicinesList instance = new MedicinesList();

    private ListView mListView;

    public static MedicinesList sharedInstance() {
        return instance;
    }

    @Override
    public void load() {
        RealmResults<Medicine> list = mRealm.where(Medicine.class).findAllAsync();
        list.addChangeListener(this);
    }

    @Override
    public void setView(ListView listView) {
        mListView = listView;
    }

    @Override
    public CharSequence getTitle(Context context) {
        return context.getString(R.string.title_activity_medicines);
    }

    @Override
    public void onChange(RealmResults<Medicine> element) {
        if (null != mListView) {
            mListView.setItems(element);
        }
    }

    @Override
    public void addNew(Context context) {
        Intent intent = new Intent(context, EditorActivity.class);
        intent.putExtra(EditorActivity.EXTRA_EDITING_FOR, Presenter.MEDICINE);
        context.startActivity(intent);
    }

    @Override
    public void delete(Context context, int id) {

    }

    public interface MedicinesListView extends ListView<Medicine> {
    }
}
