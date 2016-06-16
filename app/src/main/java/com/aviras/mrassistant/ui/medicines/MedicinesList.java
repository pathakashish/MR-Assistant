package com.aviras.mrassistant.ui.medicines;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.Medicine;
import com.aviras.mrassistant.ui.BasePresenter;
import com.aviras.mrassistant.ui.Presenter;
import com.aviras.mrassistant.ui.editors.EditorActivity;
import com.aviras.mrassistant.ui.lists.ListPresenter;
import com.aviras.mrassistant.ui.lists.ListView;
import com.aviras.mrassistant.ui.utils.FtsUtil;

import java.util.List;

import io.realm.Case;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Presenter for {@link Medicine}s list
 * <p/>
 * Created by ashish on 14/6/16.
 */
public class MedicinesList extends BasePresenter implements ListPresenter<Medicine>, RealmChangeListener<RealmResults<Medicine>> {

    private static final MedicinesList instance = new MedicinesList();
    private static final String LOG_TAG = MedicinesList.class.getSimpleName();

    private ListView mListView;

    public static MedicinesList sharedInstance() {
        return instance;
    }

    @Override
    public void load(CharSequence searchString) {
        Log.v(LOG_TAG, "load - searchString: " + searchString);
        List<CharSequence> ftsList = FtsUtil.getAllPossibleSearchStrings(searchString);
        RealmQuery<Medicine> query = mRealm.where(Medicine.class);
        for (CharSequence search : ftsList) {
            query = query.contains("name", search.toString(), Case.INSENSITIVE)
                    .or()
                    .contains("description", search.toString(), Case.INSENSITIVE);
        }
        RealmResults<Medicine> list = query.findAllAsync();
        list.addChangeListener(this);
    }

    @Override
    public void setView(ListView listView) {
        Log.v(LOG_TAG, "setView - listView: " + listView);
        mListView = listView;
    }

    @Override
    public CharSequence getTitle(Context context) {
        return context.getString(R.string.title_activity_medicines);
    }

    @Override
    public void onChange(RealmResults<Medicine> element) {
        Log.v(LOG_TAG, "onChange - element.size(): " + element.size());
        if (null != mListView) {
            mListView.setItems(element);
        }
    }

    @Override
    public void addNew(Context context) {
        Log.v(LOG_TAG, "addNew");
        Intent intent = new Intent(context, EditorActivity.class);
        intent.putExtra(EditorActivity.EXTRA_EDITING_FOR, Presenter.MEDICINE);
        context.startActivity(intent);
    }

    @Override
    public void delete(Context context, Medicine item) {
        Log.v(LOG_TAG, "delete - " + item);
        mRealm.beginTransaction();
        item.deleteFromRealm();
        mRealm.commitTransaction();
    }

    public interface MedicinesListView extends ListView<Medicine> {
    }
}
