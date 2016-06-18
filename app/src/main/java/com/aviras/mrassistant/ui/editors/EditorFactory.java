package com.aviras.mrassistant.ui.editors;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;

/**
 * Created by ashish on 8/6/16.
 */
public class EditorFactory {

    public static final int VIEW_TYPE_TEXT_FIELD_EDITOR = 0;
    public static final int VIEW_TYPE_LIST_EDITOR = 1;

    public static TextFieldEditor newTextFieldEditor(Context context,
                                                     int id,
                                                     @StringRes int nameResId,
                                                     CharSequence defaultValue,
                                                     int lineCount) {
        return newTextFieldEditor(id, 0 == nameResId ? null : context.getString(nameResId),
                defaultValue,
                lineCount);
    }

    public static TextFieldEditor newTextFieldEditor(int id,
                                                     @Nullable CharSequence name,
                                                     @Nullable CharSequence defaultValue,
                                                     int lineCount) {
        TextFieldEditor editor = new TextFieldEditor(id, VIEW_TYPE_TEXT_FIELD_EDITOR, name);
        editor.setValue(defaultValue);
        editor.setLineCount(lineCount);
        return editor;
    }

    public static ListEditor newListEditor(int id,
                                           @Nullable CharSequence name,
                                           ListEditor.ListEditorAdapter adapter,
                                           RecyclerView.LayoutManager layoutManager) {
        ListEditor editor = new ListEditor(id, VIEW_TYPE_LIST_EDITOR, name, adapter,
                layoutManager, true);
        return editor;
    }
}
