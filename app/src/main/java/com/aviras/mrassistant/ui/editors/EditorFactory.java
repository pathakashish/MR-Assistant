package com.aviras.mrassistant.ui.editors;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import java.util.List;

/**
 * Created by ashish on 8/6/16.
 */
public class EditorFactory {

    public static final int VIEW_TYPE_TEXT_FIELD_EDITOR = 0;
    public static final int VIEW_TYPE_SELECTABLE_LIST_EDITOR = 1;

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

    public static SelectableListEditor newSelectableListEditor(int id,
                                                               boolean allowMultiSelect,
                                                               @Nullable CharSequence name,
                                                               List<SelectableListEditor.SelectableListEditorItem> items) {
        SelectableListEditor editor = new SelectableListEditor(id, VIEW_TYPE_SELECTABLE_LIST_EDITOR, name);
        editor.setAllowMultiSelect(allowMultiSelect);
        editor.setItems(items);
        return editor;
    }
}
