package com.aviras.mrassistant.ui.editors;

import android.content.Context;

import com.aviras.mrassistant.ui.Presenter;

import java.util.List;

import io.realm.RealmObject;

/**
 * Presenter interface for presenting {@link Editor} in {@link EditorFragment}
 * <p/>
 * Created by ashish on 10/6/16.
 */
public interface EditorPresenter<T extends RealmObject> extends Presenter {
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
     * @param id      id of the object shown in editor
     */
    void saveOrUpdateObject(List<Editor> editors, int id);
}
