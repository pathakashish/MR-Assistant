package com.aviras.mrassistant.ui.medicines;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.EditorInfo;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.Medicine;
import com.aviras.mrassistant.data.models.Unit;
import com.aviras.mrassistant.ui.editors.Editor;
import com.aviras.mrassistant.ui.editors.EditorFactory;
import com.aviras.mrassistant.ui.editors.EditorFragment;
import com.aviras.mrassistant.ui.editors.EditorPresenter;
import com.aviras.mrassistant.ui.editors.EditorView;
import com.aviras.mrassistant.ui.editors.ListEditor;
import com.aviras.mrassistant.ui.editors.TextFieldEditor;
import com.aviras.mrassistant.ui.units.UnitsAdapter;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Middle man for connecting UI and models.
 * Implements {@link EditorPresenter} so that {@link com.aviras.mrassistant.data.models.Medicine}
 * can be edited using {@link EditorFragment}
 * <p/>
 * Created by ashish on 9/6/16.
 */
public class MedicinePresenter implements EditorPresenter<Medicine> {
    private static final String LOG_TAG = "MedicinePresenter";

    private static final int ID_NAME = 1;
    private static final int ID_DESCRIPTION = 2;
    private static final int ID_UNIT = 3;

    private static MedicinePresenter instance = new MedicinePresenter();

    public static MedicinePresenter sharedInstance() {
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
        RealmResults<Medicine> query = mRealm.where(Medicine.class).equalTo("id", id).findAllAsync();
        query.addChangeListener(new RealmChangeListener<RealmResults<Medicine>>() {
            @Override
            public void onChange(RealmResults<Medicine> element) {
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
        return context.getString(R.string.title_activity_add_medicine);
    }

    public List<Editor> getEditors(Context context, Medicine medicine) {
        List<Editor> editors = new ArrayList<>();

        TextFieldEditor name = EditorFactory.newTextFieldEditor(context, ID_NAME, R.string.medicine_name_here, "", 1);
        if (null != medicine) {
            name.setValue(medicine.getName());
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
        name.setImeOption(EditorInfo.IME_ACTION_DONE);
        editors.add(name);


        TextFieldEditor description = EditorFactory.newTextFieldEditor(context, ID_DESCRIPTION, R.string.any_medicine_info, "", 5);
        if (null != medicine) {
            description.setValue(medicine.getDescription());
        }
        description.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_LONG_MESSAGE | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
        description.setImeOption(EditorInfo.IME_ACTION_NONE);
        editors.add(description);

        final UnitsAdapter unitsAdapter = new UnitsAdapter(UnitsAdapter.MODE_SELECTION);
        ListEditor units = EditorFactory.newListEditor(ID_UNIT, context.getString(R.string.units),
                unitsAdapter, new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        if (null != medicine) {
            unitsAdapter.setSupportedUnits(medicine.getSupportedUnits());
        }
        final Realm realm = Realm.getDefaultInstance();
        RealmResults<Unit> result = realm.where(Unit.class).findAllAsync();
        result.addChangeListener(new RealmChangeListener<RealmResults<Unit>>() {
            @Override
            public void onChange(RealmResults<Unit> element) {
                RealmList<Unit> units = new RealmList<>();
                units.addAll(element);
                unitsAdapter.setUnits(units);
                unitsAdapter.notifyDataSetChanged();
            }
        });
        units.setValidator(new Editor.Validator<ListEditor>(units) {
            @Override
            public boolean validate() {
                RecyclerView.Adapter adapter = ((ListEditor) getField()).getAdapter();
                if (adapter instanceof UnitsAdapter) {
                    RealmList<Unit> supportedUnits = ((UnitsAdapter) adapter).getSelectedUnits();
                    return supportedUnits.size() > 0;
                } else {
                    Log.w(LOG_TAG, "Wrong adapter set for units");
                }
                return false;
            }
        }, context.getString(R.string.error_please_select_atlease_one_unit));
        editors.add(units);

        return editors;
    }

    @Override
    public void saveOrUpdateObject(List<Editor> editors, int id) {
        Medicine medicine = new Medicine();
        medicine.setId(id);
        for (Editor editor : editors) {
            switch (editor.getId()) {
                case ID_NAME:
                    CharSequence name = ((TextFieldEditor) editor).getValue();
                    String nameStr = null == name ? null : name.toString();
                    medicine.setName(nameStr);
                    break;
                case ID_DESCRIPTION:
                    CharSequence desc = ((TextFieldEditor) editor).getValue();
                    String descStr = null == desc ? null : desc.toString();
                    medicine.setDescription(descStr);
                    break;
                case ID_UNIT:
                    RecyclerView.Adapter adapter = ((ListEditor) editor).getAdapter();
                    if (adapter instanceof UnitsAdapter) {
                        RealmList<Unit> supportedUnits = ((UnitsAdapter) adapter).getSelectedUnits();
                        medicine.setSupportedUnits(supportedUnits);
                    } else {
                        Log.w(LOG_TAG, "Wrong adapter set for units");
                    }
                    break;
            }
        }
        saveOrUpdate(medicine);
    }

    @Override
    public Class<? extends Medicine> getRealmClass() {
        return Medicine.class;
    }

    private void saveOrUpdate(Medicine object) {
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
