package com.aviras.mrassistant.ui;

import android.content.Context;
import android.os.Bundle;

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

    /**
     * Return a bundle containing state of this presenter. This method will be called by view to store
     * state before it gets destroyed and view will call {@link Presenter#setState(Bundle)} to restore
     * the state. When view calls {@link Presenter#setState(Bundle)}, it will expect the {@link Presenter}
     * to call appropriate method to update UI.
     *
     * @return a {@link Bundle} containing current state
     */
    Bundle getState();

    /**
     * View will call this method to set the last known state and will expect {@link Presenter} to
     * call appropriate method to update UI.
     *
     * @param state {@link  Bundle} containing last known state which view got by calling {@link Presenter#getState()}
     */
    void setState(Bundle state);
}
