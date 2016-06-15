package com.aviras.mrassistant.ui.units;

import android.content.Context;
import android.os.Bundle;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.Unit;
import com.aviras.mrassistant.ui.lists.ListFragment;

import io.realm.RealmResults;

/**
 * Show {@link com.aviras.mrassistant.data.models.Unit}s list
 * <p/>
 * Created by ashish on 15/6/16.
 */
public class UnitsListFragment extends ListFragment<Unit> implements UnitsList.UnitsListView {

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

    }
}
