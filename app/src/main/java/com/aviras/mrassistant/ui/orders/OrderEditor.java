package com.aviras.mrassistant.ui.orders;

import android.content.Context;
import android.os.Bundle;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.Order;
import com.aviras.mrassistant.logger.Log;
import com.aviras.mrassistant.ui.BasePresenter;
import com.aviras.mrassistant.ui.editors.Editor;
import com.aviras.mrassistant.ui.editors.EditorPresenter;
import com.aviras.mrassistant.ui.editors.EditorView;

import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Presenter for presenting edit order option
 * <p/>
 * Created by ashish on 19/6/16.
 */
public class OrderEditor extends BasePresenter implements EditorPresenter<Order>, RealmChangeListener<RealmResults<Order>> {
    private static final String LOG_TAG = OrderEditor.class.getSimpleName();
    private static final String KEY_ORDER = "order";

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
        Log.v(LOG_TAG, "getEditors - medicine: " + order);
        return null;
    }

    @Override
    public void saveOrUpdateObject(List<Editor> editors, int id) {
        Log.v(LOG_TAG, "saveOrUpdateObject - editors: " + editors + ", id: " + id);
        saveOrUpdate(createOrderFormEditors(editors, id));

    }

    private Order createOrderFormEditors(List<Editor> editors, int id) {
        return null;
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
}
