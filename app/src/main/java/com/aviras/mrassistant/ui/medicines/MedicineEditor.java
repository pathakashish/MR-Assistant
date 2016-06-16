package com.aviras.mrassistant.ui.medicines;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.EditorInfo;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.Medicine;
import com.aviras.mrassistant.data.models.Unit;
import com.aviras.mrassistant.ui.BasePresenter;
import com.aviras.mrassistant.ui.editors.Editor;
import com.aviras.mrassistant.ui.editors.EditorFactory;
import com.aviras.mrassistant.ui.editors.EditorFragment;
import com.aviras.mrassistant.ui.editors.EditorPresenter;
import com.aviras.mrassistant.ui.editors.EditorView;
import com.aviras.mrassistant.ui.editors.ListEditor;
import com.aviras.mrassistant.ui.editors.TextFieldEditor;
import com.aviras.mrassistant.ui.units.UnitsAdapter;

import java.lang.ref.WeakReference;
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
public class MedicineEditor extends BasePresenter implements EditorPresenter<Medicine>, RealmChangeListener<RealmResults<Medicine>> {
    private static final String LOG_TAG = "MedicineEditor";

    private static final int ID_NAME = 1;
    private static final int ID_DESCRIPTION = 2;
    private static final int ID_UNIT = 3;
    private static final String KEY_MEDICINE = "medicine";

    private static MedicineEditor instance = new MedicineEditor();

    public static MedicineEditor sharedInstance() {
        return instance;
    }

    private EditorView mEditView;

    @Override
    protected void onDatabaseOpened() {
        super.onDatabaseOpened();
        AllUnitsMonitor.sharedInstance().init(mRealm);
    }

    @Override
    public Bundle getState(List<Editor> editors, int id) {
        Bundle state = new Bundle();
        state.putParcelable(KEY_MEDICINE, createMedicineFormEditors(editors, id));
        return state;
    }

    @Override
    public void setState(Context context, Bundle state) {
        if (null != mEditView) {
            Medicine medicine = state.getParcelable(KEY_MEDICINE);
            mEditView.showEditors(getEditors(context, medicine));
        }
    }

    @Override
    public void setView(EditorView editorView) {
        mEditView = editorView;
    }

    @Override
    public void load(final Context context, int id) {
        RealmResults<Medicine> query = mRealm.where(Medicine.class).equalTo("id", id).findAllAsync();
        query.addChangeListener(this);
    }

    @Override
    public void onChange(RealmResults<Medicine> element) {
        Medicine medicine;
        if (element.size() > 0) {
            medicine = element.get(0);
        } else {
            medicine = null;
        }
        if (null != mEditView && mEditView.getContext() != null) {
            mEditView.showEditors(getEditors(mEditView.getContext(), medicine));
        }
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

        final UnitsAdapter unitsAdapter = new UnitsAdapter();
        final ListEditor units = EditorFactory.newListEditor(ID_UNIT, context.getString(R.string.units),
                unitsAdapter, new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        final AllUnitsMonitor.OnAllUnitsListChangedListener unitsListChangeListener = new AllUnitsMonitor.OnAllUnitsListChangedListener() {

            @Override
            public void onListChange(RealmList<Unit> allUnits) {
                Log.v(LOG_TAG, "onListChange - " + allUnits.size());
                RealmList<Unit> units = new RealmList<>();
                units.addAll(allUnits);
                unitsAdapter.setUnits(units);
                unitsAdapter.notifyDataSetChanged();
            }
        };
        AllUnitsMonitor.sharedInstance().setChangeListener(unitsListChangeListener);
        RealmList<Unit> allUnits = new RealmList<>();
        if (null != AllUnitsMonitor.sharedInstance().mAllUnits) {
            synchronized (AllUnitsMonitor.sharedInstance().mAllUnits) {
                allUnits.addAll(AllUnitsMonitor.sharedInstance().mAllUnits);
            }
        }
        unitsAdapter.setUnits(allUnits);
        if (null != medicine) {
            unitsAdapter.setSupportedUnits(medicine.getSupportedUnits());
        }
        unitsAdapter.notifyDataSetChanged();
        units.setValidator(new Editor.Validator<ListEditor>(units) {
            @Override
            public boolean validate() {
                RecyclerView.Adapter adapter = getField().getAdapter();
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
        saveOrUpdate(createMedicineFormEditors(editors, id));
    }

    private Medicine createMedicineFormEditors(List<Editor> editors, int id) {
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
        return medicine;
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

    public static class AllUnitsMonitor implements RealmChangeListener<RealmResults<Unit>> {

        private static final String LOG_TAG = AllUnitsMonitor.class.getSimpleName();

        static AllUnitsMonitor instance = new AllUnitsMonitor();
        WeakReference<OnAllUnitsListChangedListener> changeListenerRef;

        static AllUnitsMonitor sharedInstance() {
            return instance;
        }

        final RealmList<Unit> mAllUnits = new RealmList<>();

        public void init(Realm realm) {
            final RealmResults<Unit> allUnits = realm.where(Unit.class).findAllAsync();
            allUnits.addChangeListener(this);
        }

        @Override
        public void onChange(RealmResults<Unit> element) {
            Log.v(LOG_TAG, "onChange - " + element.size());
            synchronized (mAllUnits) {
                mAllUnits.clear();
                mAllUnits.addAll(element);
                if (null != changeListenerRef && null != changeListenerRef.get()) {
                    changeListenerRef.get().onListChange(mAllUnits);
                }
            }
        }

        public void setChangeListener(OnAllUnitsListChangedListener onAllUnitsListChangedListener) {
            changeListenerRef = new WeakReference<>(onAllUnitsListChangedListener);
        }

        private interface OnAllUnitsListChangedListener {
            void onListChange(RealmList<Unit> allUnits);
        }
    }
}
