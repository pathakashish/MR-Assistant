package com.aviras.mrassistant.ui.orders;

import android.content.Context;
import android.content.Intent;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.Order;
import com.aviras.mrassistant.logger.Log;
import com.aviras.mrassistant.ui.BasePresenter;
import com.aviras.mrassistant.ui.Presenter;
import com.aviras.mrassistant.ui.editors.EditorActivity;
import com.aviras.mrassistant.ui.lists.ListPresenter;
import com.aviras.mrassistant.ui.lists.ListView;
import com.aviras.mrassistant.ui.utils.FtsUtil;

import java.util.List;

import io.realm.Case;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Presenter for {@link Order}s list
 * <p/>
 * Created by ashish on 14/6/16.
 */
public class OrdersList extends BasePresenter implements ListPresenter<Order>, RealmChangeListener<RealmResults<Order>> {

    private static final OrdersList instance = new OrdersList();
    private static final String LOG_TAG = OrdersList.class.getSimpleName();

    private ListView mListView;
    private RealmResults<Order> mCurrentList;

    public static OrdersList sharedInstance() {
        return instance;
    }

    @Override
    public void load(CharSequence searchString) {
        Log.v(LOG_TAG, "load - searchString: " + searchString);
        List<CharSequence> ftsList = FtsUtil.getAllPossibleSearchStrings(searchString);
        RealmQuery<Order> query = mRealm.where(Order.class);
        for (CharSequence search : ftsList) {
            query = query.contains("doctor.name", search.toString(), Case.INSENSITIVE)
                    .or()
                    .contains("items.medicine.name", search.toString(), Case.INSENSITIVE)
                    .or()
                    .contains("items.note", search.toString(), Case.INSENSITIVE);
        }
        if (null != mCurrentList) {
            mCurrentList.removeChangeListener(this);
        }
        mCurrentList = query.findAllAsync();
        mCurrentList.addChangeListener(this);
    }

    @Override
    public void setView(ListView listView) {
        Log.v(LOG_TAG, "setView - listView: " + listView);
        mListView = listView;
    }

    @Override
    public CharSequence getTitle(Context context) {
        return context.getString(R.string.title_activity_orders);
    }

    @Override
    public void onChange(RealmResults<Order> element) {
        Log.v(LOG_TAG, this + " " + "onChange - element.size(): " + element.size());
        if (null != mListView) {
            mListView.setItems(element);
        }
    }

    @Override
    public void addNew(Context context) {
        Log.v(LOG_TAG, "addNew");
        Intent intent = new Intent(context, EditorActivity.class);
        intent.putExtra(EditorActivity.EXTRA_EDITING_FOR, Presenter.ORDER);
        context.startActivity(intent);
    }

    @Override
    public void delete(Context context, Order item) {
        Log.v(LOG_TAG, "delete - " + item);
        mRealm.beginTransaction();
        item.deleteFromRealm();
        mRealm.commitTransaction();
    }

    public void deliver(Context context, Order order) {
        mRealm.beginTransaction();
        order.setActualDeliveryDate(System.currentTimeMillis());
        mRealm.commitTransaction();
    }

    public interface OrdersListView extends ListView<Order> {
    }
}
