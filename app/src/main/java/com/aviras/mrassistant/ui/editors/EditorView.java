package com.aviras.mrassistant.ui.editors;

import android.content.Context;

import java.util.List;

/**
 * Implement this interface to show {@link Editor}s presented by {@link EditorPresenter}
 * <p/>
 * Created by ashish on 11/6/16.
 */
public interface EditorView {
    void showEditors(List<Editor> editors);

    Context getContext();

    List<Editor> getEditors();
}
