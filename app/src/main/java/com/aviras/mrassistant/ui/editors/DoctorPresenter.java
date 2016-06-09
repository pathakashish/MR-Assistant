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
public class DoctorPresenter implements EditorPresenter {
    private static DoctorPresenter instance = new DoctorPresenter();

    public static DoctorPresenter sharedInstance() {
        return instance;
    }

    @Override
    public CharSequence getTitle(Context context) {
        return context.getString(R.string.title_activity_add_doctor);
    }

    public List<Editor> getEditors(Context context) {
        List<Editor> editors = new ArrayList<>();

        TextFieldEditor name = EditorFactory.newTextFieldEditor(context, R.string.doctors_name_here, "", 1);
        name.setRequired(true);
        name.setValidator(new Editor.Validator<TextFieldEditor>(name) {

            @Override
            public boolean validate() {
                TextFieldEditor text = getField();
                return text.isRequired() && !TextUtils.isEmpty(text.getValue());
            }
        }, context.getString(R.string.error_name_required));
        name.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME);
        name.setImeOption(EditorInfo.IME_ACTION_NEXT);
        editors.add(name);

        TextFieldEditor contactNumber = EditorFactory.newTextFieldEditor(context, R.string.mob_phone_etc, "", 1);
        contactNumber.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        contactNumber.setImeOption(EditorInfo.IME_ACTION_NEXT);
        editors.add(contactNumber);

        TextFieldEditor address = EditorFactory.newTextFieldEditor(context, R.string.delivery_address, "", 5);
        address.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_POSTAL_ADDRESS | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
        address.setImeOption(EditorInfo.IME_ACTION_NONE);
        editors.add(address);

        TextFieldEditor note = EditorFactory.newTextFieldEditor(context, R.string.any_other_info, "", 3);
        note.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_LONG_MESSAGE | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
        note.setImeOption(EditorInfo.IME_ACTION_NONE);
        editors.add(note);

        return editors;
    }
}
