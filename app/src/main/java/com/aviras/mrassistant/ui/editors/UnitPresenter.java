package com.aviras.mrassistant.ui.editors;

import android.content.Context;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;

import com.aviras.mrassistant.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Middle man for connecting UI and models
 * <p/>
 * Created by ashish on 9/6/16.
 */
public class UnitPresenter implements EditorPresenter {
    private static UnitPresenter instance = new UnitPresenter();

    public static UnitPresenter sharedInstance() {
        return instance;
    }

    @Override
    public CharSequence getTitle(Context context) {
        return context.getString(R.string.title_activity_add_unit);
    }

    public List<Editor> getEditors(Context context) {
        List<Editor> editors = new ArrayList<>();

        TextFieldEditor name = EditorFactory.newTextFieldEditor(context, R.string.unit_name, "", 1);
        name.setRequired(true);
        name.setValidator(new Editor.Validator<TextFieldEditor>(name) {

            @Override
            public boolean validate() {
                TextFieldEditor text = getField();
                return text.isRequired() && !TextUtils.isEmpty(text.getValue());
            }
        }, context.getString(R.string.error_unit_name_required));
        name.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME);
        name.setImeOption(EditorInfo.IME_ACTION_DONE);
        editors.add(name);

        return editors;
    }
}
