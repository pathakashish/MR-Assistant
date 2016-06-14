package com.aviras.mrassistant.ui.units;

import android.content.Context;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.Unit;
import com.aviras.mrassistant.ui.lists.BaseList;

/**
 * Presenter for {@link Unit}s list
 * <p/>
 * Created by ashish on 14/6/16.
 */
public class UnitsList extends BaseList<Unit> {

    public UnitsList() {
        super(Unit.class);
    }

    @Override
    public CharSequence getTitle(Context context) {
        return context.getString(R.string.title_activity_units);
    }
}
