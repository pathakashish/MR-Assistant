package com.aviras.mrassistant.ui;

import android.content.Context;

import com.aviras.mrassistant.ui.editors.EditorFragment;

/**
 * Responsibilities of presenter in general
 * <p/>
 * Created by ashish on 13/6/16.
 */
public interface Presenter {
    String DOCTOR = "doctor";
    String MEDICINE = "medicine";
    String UNIT = "unit";

    /**
     * Provide the title for the {@link EditorFragment}
     *
     * @param context
     * @return title for the {@link EditorFragment}
     */
    CharSequence getTitle(Context context);

    /**
     * Remove all change listeners registered by this presenter.
     */
    void removeAllChangeListeners();
}
