package com.aviras.mrassistant.ui.lists;

import android.content.Context;
import android.os.Bundle;

import com.aviras.mrassistant.R;

/**
 * Show {@link com.aviras.mrassistant.data.models.Unit}s list
 * <p/>
 * Created by ashish on 15/6/16.
 */
public class UnitsListFragment extends ListFragment {

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
}
