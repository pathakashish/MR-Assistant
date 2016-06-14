package com.aviras.mrassistant.ui.doctors;

import android.content.Context;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.Doctor;
import com.aviras.mrassistant.ui.lists.BaseList;

/**
 * Presenter for {@link Doctor}s list
 * <p/>
 * Created by ashish on 14/6/16.
 */
public class DoctorsList extends BaseList<Doctor> {

    public DoctorsList() {
        super(Doctor.class);
    }

    @Override
    public CharSequence getTitle(Context context) {
        return context.getString(R.string.title_activity_doctors);
    }
}
