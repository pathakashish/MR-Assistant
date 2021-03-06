package com.aviras.mrassistant.ui.units;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.Unit;
import com.aviras.mrassistant.logger.Log;
import com.aviras.mrassistant.ui.BasePresenter;
import com.aviras.mrassistant.ui.editors.Editor;
import com.aviras.mrassistant.ui.editors.EditorFactory;
import com.aviras.mrassistant.ui.editors.EditorFragment;
import com.aviras.mrassistant.ui.editors.EditorPresenter;
import com.aviras.mrassistant.ui.editors.EditorView;
import com.aviras.mrassistant.ui.editors.TextFieldEditor;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Middle man for connecting UI and models
 * Implements {@link EditorPresenter} so that {@link com.aviras.mrassistant.data.models.Unit} can be
 * edited using {@link EditorFragment}
 * <p/>
 * Created by ashish on 9/6/16.
 */
public class UnitEditor extends BasePresenter implements EditorPresenter<Unit>, RealmChangeListener<RealmResults<Unit>> {
    private static final String LOG_TAG = "UnitEditor";
    private static final int ID_NAME = 1;
    private static final String KEY_UNIT = "unit";
    private static UnitEditor instance = new UnitEditor();

    public static UnitEditor sharedInstance() {
        return instance;
    }

    private EditorView mEditView;

    @Override
    public Bundle getState(List<Editor> editors, int id) {
        Log.v(LOG_TAG, "getState - editors: " + editors + ", id" + id);
        Bundle state = new Bundle();
        state.putParcelable(KEY_UNIT, createUnitFromEditors(editors, id));
        return state;
    }

    @Override
    public void setState(Context context, Bundle state) {
        Log.v(LOG_TAG, "setState - state: " + state);
        if (null != mEditView) {
            Unit unit = state.getParcelable(KEY_UNIT);
            mEditView.showEditors(getEditors(context, unit));
        }
    }

    @Override
    public void setView(EditorView editorView) {
        Log.v(LOG_TAG, "setView - editorView: " + editorView);
        mEditView = editorView;
    }

    @Override
    public void load(final Context context, int id) {
        Log.v(LOG_TAG, "load - id: " + id);
        RealmResults<Unit> query = mRealm.where(Unit.class).equalTo("id", id).findAllAsync();
        query.removeChangeListener(this);
        query.addChangeListener(this);
        if (null != mEditView) {
            mEditView.showEditors(getEditors(context, query.isLoaded() && query.size() > 0 ? query.get(0) : null));
        }
    }

    @Override
    public void onChange(RealmResults<Unit> element) {
        Log.v(LOG_TAG, "onChange - element.size(): " + element.size());
        Unit unit;
        if (element.size() > 0) {
            unit = element.get(0);
        } else {
            unit = null;
        }
        if (null != mEditView && mEditView.getContext() != null) {
            List<Editor> editors;
            if (null == unit) {
                editors = mEditView.getEditors();
            } else {
                editors = null;
            }
            if (null == editors) {
                editors = getEditors(mEditView.getContext(), unit);
            }
            mEditView.showEditors(editors);
        }
    }

    @Override
    public CharSequence getTitle(Context context) {
        return context.getString(R.string.title_activity_add_unit);
    }

    @Override
    public List<Editor> getEditors(Context context, Unit unit) {
        Log.v(LOG_TAG, "getEditors - unit: " + unit);
        List<Editor> editors = new ArrayList<>();

        TextFieldEditor name = EditorFactory.newTextFieldEditor(context, ID_NAME, R.string.unit_name, "", 1);
        if (null != unit) {
            name.setValue(unit.getName());
        }
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

    @Override
    public void saveOrUpdateObject(List<Editor> editors, int id) {
        Log.v(LOG_TAG, "saveOrUpdateObject - editors: " + editors + ", id: " + id);
        saveOrUpdate(createUnitFromEditors(editors, id));
    }

    private Unit createUnitFromEditors(List<Editor> editors, int id) {
        Log.v(LOG_TAG, "createUnitFromEditors - editors: " + editors + ", id: " + id);
        Unit unit = new Unit();
        unit.setId(id);
        for (Editor editor : editors) {
            switch (editor.getId()) {
                case ID_NAME:
                    CharSequence val = ((TextFieldEditor) editor).getValue();
                    String valStr = null == val ? null : val.toString();
                    unit.setName(valStr);
                    break;
            }
        }
        return unit;
    }

    private void saveOrUpdate(Unit object) {
        Log.v(LOG_TAG, "saveOrUpdate - object: " + object);
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
