package com.aviras.mrassistant.ui.lists;

import android.content.Context;

import com.aviras.mrassistant.R;

/**
 * Show {@link com.aviras.mrassistant.data.models.Medicine}s list
 * <p/>
 * Created by ashish on 15/6/16.
 */
public class UnitsListFragment extends ListFragment {
    @Override
    public CharSequence getTitle(Context context) {
        return context.getString(R.string.title_activity_medicines);
    }
}
