package com.aviras.mrassistant.ui;

import android.content.Context;

import com.aviras.mrassistant.ui.editors.EditorView;

/**
 * Responsibilities of presenter in general
 * <p/>
 * Created by ashish on 13/6/16.
 */
public interface Presenter {

    /**
     * Set the editor view. This view will get all view related callbaccks
     *
     * @param editorView
     */
    void setView(EditorView editorView);

    /**
     * Load single item with given id and notify view about this
     *
     * @param context
     * @param id      id of single item to be loaded
     */
    void load(Context context, int id);

    /**
     * Opens the underlying database
     */
    void openDatabase();

    /**
     * Opens the underlying database
     */
    void closeDatabase();
}
