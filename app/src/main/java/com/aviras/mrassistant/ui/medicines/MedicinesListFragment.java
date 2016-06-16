package com.aviras.mrassistant.ui.medicines;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.Medicine;
import com.aviras.mrassistant.ui.lists.ListAdapter;
import com.aviras.mrassistant.ui.lists.ListFragment;

import io.realm.RealmResults;

/**
 * Show {@link com.aviras.mrassistant.data.models.Medicine}s list
 * <p/>
 * Created by ashish on 15/6/16.
 */
public class MedicinesListFragment extends ListFragment<Medicine> implements MedicinesList.MedicinesListView {

    private static final String LOG_TAG = MedicinesListFragment.class.getSimpleName();
    private ListAdapter mAdapter = new MedicinesListAdapter();

    public static ListFragment newInstance(String listFor) {
        MedicinesListFragment fragment = new MedicinesListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LIST_FOR, listFor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public CharSequence getTitle(Context context) {
        return context.getString(R.string.title_activity_medicines);
    }

    @Override
    public void setItems(RealmResults<Medicine> medicines) {
        Log.v(LOG_TAG, "setItems - medicines: " + medicines);
        if (null != mAdapter) {
            mAdapter.setItems(medicines);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected ListAdapter<MedicinesListAdapter.ViewHolder, Medicine> getListAdapter(Context context) {
        Log.v(LOG_TAG, "getListAdapter");
        return mAdapter;
    }
}
