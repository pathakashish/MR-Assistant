package com.aviras.mrassistant.ui.doctors;

import android.content.Context;
import android.os.Bundle;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.Doctor;
import com.aviras.mrassistant.ui.lists.ListAdapter;
import com.aviras.mrassistant.ui.lists.ListFragment;

import io.realm.RealmResults;

/**
 * Show {@link com.aviras.mrassistant.data.models.Doctor}s list
 * <p/>
 * Created by ashish on 15/6/16.
 */
public class DoctorsListFragment extends ListFragment<Doctor> implements DoctorsList.DoctorsListView {

    private ListAdapter mAdapter = new DoctorsListAdapter();

    public static ListFragment newInstance(String listFor) {
        DoctorsListFragment fragment = new DoctorsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LIST_FOR, listFor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public CharSequence getTitle(Context context) {
        return context.getString(R.string.title_activity_doctors);
    }

    @Override
    public void setItems(RealmResults<Doctor> doctors) {
        if (null != mAdapter) {
            mAdapter.setItems(doctors);
        }
    }

    @Override
    protected ListAdapter<DoctorsListAdapter.ViewHolder, Doctor> getListAdapter(Context context) {
        return mAdapter;
    }
}
