package com.aviras.mrassistant.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Simple {@link DatePickerDialog} shown in {@link DialogFragment}
 * <p/>
 * Created by ashish on 19/6/16.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public static final String TAG = DatePickerFragment.class.getSimpleName();

    private DatePickerDialog.OnDateSetListener mDateSelectedListener;
    private long mDate;

    public DatePickerFragment() {

    }

    public DatePickerFragment(DatePickerDialog.OnDateSetListener dateSelectedListener, long date) {
        mDateSelectedListener = dateSelectedListener;
        mDate = date;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(mDate);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        if (null != mDateSelectedListener) {
            mDateSelectedListener.onDateSet(view, year, month, day);
        }
    }
}