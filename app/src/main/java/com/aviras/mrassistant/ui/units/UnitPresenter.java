package com.aviras.mrassistant.ui.units;

import android.content.Context;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.Unit;
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
 * Middle man for connecting UI and models
 * Implements {@link EditorPresenter} so that {@link com.aviras.mrassistant.data.models.Unit} can be
 * edited using {@link EditorFragment}
 * <p/>
 * Created by ashish on 9/6/16.
 */
public class UnitPresenter implements EditorPresenter<Unit> {
    private static final String LOG_TAG = "UnitPresenter";
    private static final int ID_NAME = 1;
    private static UnitPresenter instance = new UnitPresenter();

    public static UnitPresenter sharedInstance() {
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
        RealmResults<Unit> query = mRealm.where(Unit.class).equalTo("id", id).findAllAsync();
        query.addChangeListener(new RealmChangeListener<RealmResults<Unit>>() {
            @Override
            public void onChange(RealmResults<Unit> element) {
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
        return context.getString(R.string.title_activity_add_unit);
    }

    @Override
    public List<Editor> getEditors(Context context, Unit unit) {
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
        saveOrUpdate(createUnitFromEditors(editors, id));
    }

    private Unit createUnitFromEditors(List<Editor> editors, int id) {
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
