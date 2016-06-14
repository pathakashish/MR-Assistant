package com.aviras.mrassistant.ui;

import android.content.Context;

import com.aviras.mrassistant.ui.editors.EditorFragment;

/**
 * Responsibilities of presenter in general
 * <p/>
 * Created by ashish on 13/6/16.
 */
public interface Presenter {
    /**
     * Provide the title for the {@link EditorFragment}
     *
     * @param context
     * @return title for the {@link EditorFragment}
     */
    CharSequence getTitle(Context context);

    /**
     * Opens the underlying database
     */
    void openDatabase();

    /**
     * Opens the underlying database
     */
    void closeDatabase();
}
