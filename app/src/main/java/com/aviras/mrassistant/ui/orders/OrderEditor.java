package com.aviras.mrassistant.ui.orders;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.Doctor;
import com.aviras.mrassistant.data.models.Medicine;
import com.aviras.mrassistant.data.models.Order;
import com.aviras.mrassistant.data.models.OrderItem;
import com.aviras.mrassistant.logger.Log;
import com.aviras.mrassistant.ui.BasePresenter;
import com.aviras.mrassistant.ui.editors.Editor;
import com.aviras.mrassistant.ui.editors.EditorFactory;
import com.aviras.mrassistant.ui.editors.EditorPresenter;
import com.aviras.mrassistant.ui.editors.EditorView;
import com.aviras.mrassistant.ui.editors.ListEditor;
import com.aviras.mrassistant.ui.utils.FtsUtil;
import com.aviras.mrassistant.ui.utils.VerticalSpaceItemDecoration;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Presenter for presenting edit order option
 * <p/>
 * Created by ashish on 19/6/16.
 */
public class OrderEditor extends BasePresenter implements EditorPresenter<Order>, RealmChangeListener<RealmResults<Order>> {
    private static final String LOG_TAG = OrderEditor.class.getSimpleName();
    private static final String KEY_ORDER = "order";
    private static final int ID_ORDERS = 34564;
    private MedicinesMonitor.OnMedicineListChangedListener mMedicinesListChangeListener;
    private DoctorsMonitor.OnDoctorListChangedListener mDoctorsListChangeListener;

    @Override
    public CharSequence getTitle(Context context) {
        return context.getString(R.string.note_order);
    }

    private static OrderEditor instance = new OrderEditor();

    public static OrderEditor sharedInstance() {
        return instance;
    }

    private EditorView mEditView;

    @Override
    public Bundle getState(List<Editor> editors, int id) {
        Log.v(LOG_TAG, "getState - editors: " + editors + ", id" + id);
        Bundle state = new Bundle();
        state.putParcelable(KEY_ORDER, createOrderFormEditors(editors, id));
        return state;
    }

    @Override
    public void setState(Context context, Bundle state) {
        Log.v(LOG_TAG, "setState - state: " + state);
        if (null != mEditView) {
            Order order = state.getParcelable(KEY_ORDER);
            mEditView.showEditors(getEditors(context, order));
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
        RealmResults<Order> query = mRealm.where(Order.class).equalTo("id", id).findAllAsync();
        query.removeChangeListener(this);
        query.addChangeListener(this);
        if (null != mEditView) {
            mEditView.showEditors(getEditors(context, query.isLoaded() && query.size() > 0 ? query.get(0) : null));
        }
    }

    @Override
    public void onChange(RealmResults<Order> element) {
        Log.v(LOG_TAG, "onChange - element.size(): " + element.size());
        Order order;
        if (element.size() > 0) {
            order = element.get(0);
        } else {
            order = null;
        }
        if (null != mEditView && mEditView.getContext() != null) {
            List<Editor> editors;
            if (null == order) {
                editors = mEditView.getEditors();
            } else {
                editors = null;
            }
            if (null == editors) {
                editors = getEditors(mEditView.getContext(), order);
            }
            mEditView.showEditors(editors);
        }
    }

    @Override
    public List<Editor> getEditors(Context context, Order order) {
        Log.v(LOG_TAG, "getEditors - order: " + order);

        List<Editor> editors = new ArrayList<>();

        final OrdersEditorAdapter orderAdapter = new OrdersEditorAdapter(order);
        final ListEditor units = EditorFactory.newListEditor(ID_ORDERS, "",
                orderAdapter, new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false),
                new VerticalSpaceItemDecoration(context.getResources().getDimensionPixelOffset(R.dimen.divider_height)));
        mDoctorsListChangeListener = new DoctorsMonitor.OnDoctorListChangedListener() {

            @Override
            public void onListChange(RealmList<Doctor> allMedicines) {
                Log.v(LOG_TAG, "onListChange - Doctor - " + allMedicines.size());
                RealmList<Doctor> doctors = new RealmList<>();
                doctors.addAll(allMedicines);
                orderAdapter.setDoctors(doctors);
                orderAdapter.notifyDataSetChanged();
            }
        };
        DoctorsMonitor.sharedInstance().setChangeListener(mDoctorsListChangeListener);
        DoctorsMonitor.sharedInstance().load(mRealm, "");
        mMedicinesListChangeListener = new MedicinesMonitor.OnMedicineListChangedListener() {

            @Override
            public void onListChange(RealmList<Medicine> allMedicines) {
                Log.v(LOG_TAG, "onListChange - Medicine - " + allMedicines.size());
                RealmList<Medicine> medicines = new RealmList<>();
                medicines.addAll(allMedicines);
                orderAdapter.setItems(medicines);
                orderAdapter.notifyDataSetChanged();
            }
        };
        MedicinesMonitor.sharedInstance().setChangeListener(mMedicinesListChangeListener);
        MedicinesMonitor.sharedInstance().load(mRealm, "");
        RealmList<Medicine> allMedicines = new RealmList<>();
        if (null != MedicinesMonitor.sharedInstance().mMedicines) {
            synchronized (MedicinesMonitor.sharedInstance().mMedicines) {
                allMedicines.addAll(MedicinesMonitor.sharedInstance().mMedicines);
                orderAdapter.setItems(allMedicines);
            }
        }
        orderAdapter.notifyDataSetChanged();
        units.setValidator(new Editor.Validator<ListEditor>(units) {
            @Override
            public boolean validate() {
                RecyclerView.Adapter adapter = getField().getAdapter();
                if (adapter instanceof OrdersEditorAdapter) {
                    OrdersEditorAdapter ordersEditorAdapter = ((OrdersEditorAdapter) adapter);
                    RealmList<OrderItem> orderItems = ordersEditorAdapter.getOrderItems();
                    return orderItems.size() > 0 && ordersEditorAdapter.getDoctor() != null && ordersEditorAdapter.getExpectedDeliveryDate() != 0;
                } else {
                    Log.w(LOG_TAG, "Wrong adapter set for units");
                }
                return false;
            }
        }, context.getString(R.string.error_order_editor));
        editors.add(units);

        return editors;
    }

    @Override
    public void saveOrUpdateObject(List<Editor> editors, int id) {
        Log.v(LOG_TAG, "saveOrUpdateObject - editors: " + editors + ", id: " + id);
        saveOrUpdate(createOrderFormEditors(editors, id));

    }

    private Order createOrderFormEditors(List<Editor> editors, int id) {
        Log.v(LOG_TAG, "createMedicineFormEditors - editors: " + editors + ", id: " + id);
        Order order = new Order();
        for (Editor editor : editors) {
            switch (editor.getId()) {
                case ID_ORDERS:
                    RecyclerView.Adapter adapter = ((ListEditor) editor).getAdapter();
                    if (adapter instanceof OrdersEditorAdapter) {
                        order = ((OrdersEditorAdapter) adapter).getOrder();
                    } else {
                        Log.w(LOG_TAG, "Wrong adapter set for units");
                    }
                    break;
            }
        }
        order.setId(id);
        return order;
    }

    private void saveOrUpdate(Order object) {
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

    public void loadMedicines(CharSequence text) {
        MedicinesMonitor.sharedInstance().load(mRealm, text);
    }

    public static class MedicinesMonitor implements RealmChangeListener<RealmResults<Medicine>> {

        private static final String LOG_TAG = MedicinesMonitor.class.getSimpleName();

        static MedicinesMonitor instance = new MedicinesMonitor();
        WeakReference<OnMedicineListChangedListener> changeListenerRef;
        private RealmResults<Medicine> mMedicinesProxy;

        static MedicinesMonitor sharedInstance() {
            return instance;
        }

        final RealmList<Medicine> mMedicines = new RealmList<>();

        public void load(Realm realm, CharSequence text) {
            Log.v(LOG_TAG, "init");
            if (null == mMedicinesProxy) {
                mMedicinesProxy = realm.where(Medicine.class).findAllAsync();
            }

            List<CharSequence> ftsList = FtsUtil.getAllPossibleSearchStrings(text);
            RealmQuery<Medicine> query = realm.where(Medicine.class);
            for (CharSequence search : ftsList) {
                query = query.contains("name", search.toString(), Case.INSENSITIVE)
                        .or()
                        .contains("description", search.toString(), Case.INSENSITIVE);
            }
            if (null != mMedicinesProxy) {
                mMedicinesProxy.removeChangeListener(this);
            }
            mMedicinesProxy = query.findAllAsync();
            mMedicinesProxy.addChangeListener(this);
        }

        @Override
        public void onChange(RealmResults<Medicine> element) {
            Log.v(LOG_TAG, "onChange - element.size(): " + element.size());
            synchronized (mMedicines) {
                mMedicines.clear();
                mMedicines.addAll(element);
                if (null != changeListenerRef && null != changeListenerRef.get()) {
                    changeListenerRef.get().onListChange(mMedicines);
                }
            }
        }

        public void setChangeListener(OnMedicineListChangedListener onMedicinesListChangedListener) {
            changeListenerRef = new WeakReference<>(onMedicinesListChangedListener);
        }

        private interface OnMedicineListChangedListener {
            void onListChange(RealmList<Medicine> allUnits);
        }
    }

    public static class DoctorsMonitor implements RealmChangeListener<RealmResults<Doctor>> {

        private static final String LOG_TAG = MedicinesMonitor.class.getSimpleName();

        static DoctorsMonitor instance = new DoctorsMonitor();
        WeakReference<OnDoctorListChangedListener> changeListenerRef;
        private RealmResults<Doctor> mDoctorsProxy;

        static DoctorsMonitor sharedInstance() {
            return instance;
        }

        final RealmList<Doctor> mDoctors = new RealmList<>();

        public void load(Realm realm, CharSequence text) {
            Log.v(LOG_TAG, "init");
            if (null == mDoctorsProxy) {
                mDoctorsProxy = realm.where(Doctor.class).findAllAsync();
            }

            List<CharSequence> ftsList = FtsUtil.getAllPossibleSearchStrings(text);
            RealmQuery<Doctor> query = realm.where(Doctor.class);
            for (CharSequence search : ftsList) {
                query = query.contains("name", search.toString(), Case.INSENSITIVE)
                        .or()
                        .contains("address", search.toString(), Case.INSENSITIVE);
            }
            if (null != mDoctorsProxy) {
                mDoctorsProxy.removeChangeListener(this);
            }
            mDoctorsProxy = query.findAllAsync();
            mDoctorsProxy.addChangeListener(this);
        }

        @Override
        public void onChange(RealmResults<Doctor> element) {
            Log.v(LOG_TAG, "onChange - element.size(): " + element.size());
            synchronized (mDoctors) {
                mDoctors.clear();
                mDoctors.addAll(element);
                if (null != changeListenerRef && null != changeListenerRef.get()) {
                    changeListenerRef.get().onListChange(mDoctors);
                }
            }
        }

        public void setChangeListener(OnDoctorListChangedListener onDoctorsListChangedListener) {
            changeListenerRef = new WeakReference<>(onDoctorsListChangedListener);
        }

        private interface OnDoctorListChangedListener {
            void onListChange(RealmList<Doctor> allUnits);
        }
    }
}
