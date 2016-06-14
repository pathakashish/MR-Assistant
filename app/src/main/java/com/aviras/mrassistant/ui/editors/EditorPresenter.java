package com.aviras.mrassistant.ui.editors;

import android.content.Context;
import android.os.Bundle;

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

    /**
     * Return a bundle containing state of this presenter. This method will be called by view to store
     * state before it gets destroyed and view will call {@link EditorPresenter#setState(Context, Bundle)} to restore
     * the state. When view calls {@link EditorPresenter#setState(Context, Bundle)}, it will expect the {@link EditorPresenter}
     * to call appropriate method to update UI.
     *
     * @param editors extract state from these editors
     * @param id      this is the id for which we are showing data on UI
     * @return a {@link Bundle} containing current state
     */
    Bundle getState(List<Editor> editors, int id);

    /**
     * View will call this method to set the last known state and will expect {@link EditorPresenter} to
     * call appropriate method to update UI.
     *
     * @param context
     * @param state   {@link  Bundle} containing last known state which view got by calling {@link EditorPresenter#getState(List, int)}
     */
    void setState(Context context, Bundle state);
}
