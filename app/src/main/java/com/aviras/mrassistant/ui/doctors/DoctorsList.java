package com.aviras.mrassistant.ui.doctors;

import android.content.Context;
import android.content.Intent;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.Doctor;
import com.aviras.mrassistant.ui.BasePresenter;
import com.aviras.mrassistant.ui.Presenter;
import com.aviras.mrassistant.ui.editors.EditorActivity;
import com.aviras.mrassistant.ui.lists.ListPresenter;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Presenter for {@link Doctor}s list
 * <p/>
 * Created by ashish on 14/6/16.
 */
public class DoctorsList extends BasePresenter implements ListPresenter<Doctor>, RealmChangeListener<RealmResults<Doctor>> {

    private static final DoctorsList instance = new DoctorsList();

    private ListView mListView;

    public static DoctorsList sharedInstance() {
        return instance;
    }

    @Override
    public void load() {
        RealmResults<Doctor> list = mRealm.where(Doctor.class).findAllAsync();
        list.addChangeListener(this);
    }

    @Override
    public void setView(ListView listView) {
        mListView = listView;
    }

    @Override
    public void addNew(Context context) {
        Intent intent = new Intent(context, EditorActivity.class);
        intent.putExtra(EditorActivity.EXTRA_EDITING_FOR, Presenter.DOCTOR);
        context.startActivity(intent);
    }

    @Override
    public CharSequence getTitle(Context context) {
        return context.getString(R.string.title_activity_doctors);
    }

    @Override
    public void onChange(RealmResults<Doctor> element) {
        if (null != mListView) {
            mListView.setItems(element);
        }
    }
}
