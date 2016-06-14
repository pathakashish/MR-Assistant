package com.aviras.mrassistant.ui.medicines;

import android.content.Context;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.Medicine;
import com.aviras.mrassistant.data.models.Unit;
import com.aviras.mrassistant.ui.lists.BaseList;

/**
 * Presenter for {@link Medicine}s list
 * <p/>
 * Created by ashish on 14/6/16.
 */
public class MedicinesList extends BaseList<Medicine> {

    public MedicinesList() {
        super(Medicine.class);
    }

    @Override
    public CharSequence getTitle(Context context) {
        return context.getString(R.string.title_activity_medicines);
    }
}
