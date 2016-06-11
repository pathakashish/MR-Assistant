package com.aviras.mrassistant.ui.editors;

import android.content.Context;

import java.util.List;

import io.realm.RealmObject;

/**
 * Presenter interface for presenting {@link Editor} in {@link EditorFragment}
 * <p/>
 * Created by ashish on 10/6/16.
 */
public interface EditorPresenter<T extends RealmObject> {
    /**
     * Provide the title for the {@link EditorFragment}
     *
     * @param context
     * @return title for the {@link EditorFragment}
     */
    CharSequence getTitle(Context context);

    /**
     * Get list of {@link Editor}s representing the edit fields
     *
     * @param context
     * @return list of {@link Editor}s representing the edit fields
     */
    List<Editor> getEditors(Context context, T object);

    /**
     * Save {@link List<Editor>} to persistent storage
     *
     * @param editors editors to be persisted
     */
    void saveOrUpdateObject(List<Editor> editors);

    Class<? extends T> getRealmClass();

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
