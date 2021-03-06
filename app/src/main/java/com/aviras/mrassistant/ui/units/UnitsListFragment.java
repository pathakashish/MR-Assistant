package com.aviras.mrassistant.ui.units;

import android.content.Context;
import android.os.Bundle;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.Unit;
import com.aviras.mrassistant.logger.Log;
import com.aviras.mrassistant.ui.lists.ListAdapter;
import com.aviras.mrassistant.ui.lists.ListFragment;

import io.realm.RealmResults;

/**
 * Show {@link com.aviras.mrassistant.data.models.Unit}s list
 * <p/>
 * Created by ashish on 15/6/16.
 */
public class UnitsListFragment extends ListFragment<Unit> implements UnitsList.UnitsListView {

    private static final String LOG_TAG = UnitsListFragment.class.getSimpleName();
    private ListAdapter mAdapter = new UnitsListAdapter();

    public static ListFragment newInstance(String listFor) {
        UnitsListFragment fragment = new UnitsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LIST_FOR, listFor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public CharSequence getTitle(Context context) {
        return context.getString(R.string.title_activity_units);
    }

    @Override
    public void setItems(RealmResults<Unit> units) {
        Log.v(LOG_TAG, "setItems - units: " + units);
        if (null != mAdapter) {
            mAdapter.setItems(units);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected ListAdapter<UnitsListAdapter.ViewHolder, Unit> getListAdapter(Context context) {
        Log.v(LOG_TAG, "getListAdapter");
        return mAdapter;
    }
}
