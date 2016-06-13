package com.aviras.mrassistant.ui.doctors;

import android.content.Context;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.Doctor;
import com.aviras.mrassistant.ui.editors.Editor;
import com.aviras.mrassistant.ui.editors.EditorFactory;
import com.aviras.mrassistant.ui.editors.EditorFragment;
import com.aviras.mrassistant.ui.editors.EditorPresenter;
import com.aviras.mrassistant.ui.editors.EditorView;
import com.aviras.mrassistant.ui.editors.TextFieldEditor;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Middle man for connecting UI and models.
 * Implements {@link EditorPresenter} so that {@link com.aviras.mrassistant.data.models.Doctor}
 * can be edited using {@link EditorFragment}
 * <p/>
 * Created by ashish on 9/6/16.
 */
public class DoctorPresenter implements EditorPresenter<Doctor> {
    private static final String LOG_TAG = "DoctorPresenter";
    private static DoctorPresenter instance = new DoctorPresenter();

    private static final int ID_NAME = 2;
    private static final int ID_CONTACT_NUMBER = 3;
    private static final int ID_ADDRESS = 4;
    private static final int ID_NOTE = 5;

    public static DoctorPresenter sharedInstance() {
        return instance;
    }

    private EditorView mEditView;

    private Realm mRealm;

    @Override
    public void closeDatabase() {
        mRealm.close();
    }

    @Override
    public void openDatabase() {
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public void setView(EditorView editorView) {
        mEditView = editorView;
    }

    @Override
    public void load(final Context context, int id) {
        RealmResults<Doctor> query = mRealm.where(Doctor.class).equalTo("id", id).findAllAsync();
        query.addChangeListener(new RealmChangeListener<RealmResults<Doctor>>() {
            @Override
            public void onChange(RealmResults<Doctor> element) {
                element.removeChangeListener(this);
                if (element.size() > 0) {
                    if (null != mEditView) {
                        mEditView.showEditors(getEditors(context, element.get(0)));
                    }
                } else {
                    mEditView.showEditors(getEditors(context, null));
                }
            }
        });
    }

    @Override
    public CharSequence getTitle(Context context) {
        return context.getString(R.string.title_activity_add_doctor);
    }

    public List<Editor> getEditors(Context context, Doctor doctor) {
        List<Editor> editors = new ArrayList<>();

        TextFieldEditor name = EditorFactory.newTextFieldEditor(context, ID_NAME, R.string.doctors_name_here, "", 1);
        if (null != doctor) {
            name.setValue(doctor.getName());
        }
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

        TextFieldEditor contactNumber = EditorFactory.newTextFieldEditor(context, ID_CONTACT_NUMBER, R.string.mob_phone_etc, "", 1);
        if (null != doctor) {
            contactNumber.setValue(doctor.getContactNumber());
        }
        contactNumber.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        contactNumber.setImeOption(EditorInfo.IME_ACTION_NEXT);
        editors.add(contactNumber);

        TextFieldEditor address = EditorFactory.newTextFieldEditor(context, ID_ADDRESS, R.string.delivery_address, "", 5);
        if (null != doctor) {
            address.setValue(doctor.getAddress());
        }
        address.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_POSTAL_ADDRESS | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
        address.setImeOption(EditorInfo.IME_ACTION_NONE);
        editors.add(address);

        TextFieldEditor note = EditorFactory.newTextFieldEditor(context, ID_NOTE, R.string.any_other_info, "", 3);
        if (null != doctor) {
            note.setValue(doctor.getNotes());
        }
        note.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_LONG_MESSAGE | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
        note.setImeOption(EditorInfo.IME_ACTION_NONE);
        editors.add(note);

        return editors;
    }

    @Override
    public void saveOrUpdateObject(List<Editor> editors, int id) {
        saveOrUpdate(createDoctorFromEditors(editors, id));
    }

    private Doctor createDoctorFromEditors(List<Editor> editors, int id) {
        Doctor doctor = new Doctor();
        doctor.setId(id);
        for (Editor editor : editors) {
            CharSequence val = ((TextFieldEditor) editor).getValue();
            String valStr = null == val ? null : val.toString();
            switch (editor.getId()) {
                case ID_NAME:
                    doctor.setName(valStr);
                    break;
                case ID_CONTACT_NUMBER:
                    doctor.setContactNumber(valStr);
                    break;
                case ID_ADDRESS:
                    doctor.setAddress(valStr);
                    break;
                case ID_NOTE:
                    doctor.setNotes(valStr);
                    break;
            }
        }
        return doctor;
    }

    private void saveOrUpdate(Doctor object) {
        if (object.getId() == 0) {
            Number id = mRealm.where(object.getClass()).max("id");
            if (null == id) {
                id = 1;
            } else {
                id = id.intValue() + 1;
            }
            object.setId(id.intValue());
        }
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(object);
        mRealm.commitTransaction();
    }
}
