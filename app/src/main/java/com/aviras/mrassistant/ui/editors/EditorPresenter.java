package com.aviras.mrassistant.ui.editors;

import android.content.Context;

import java.util.List;

/**
 * Created by ashish on 10/6/16.
 */
public interface EditorPresenter {
    CharSequence getTitle(Context context);

    List<Editor> getEditors(Context context);
}
