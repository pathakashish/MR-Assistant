package com.aviras.mrassistant.ui.orders;

import android.content.Context;
import android.os.Bundle;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.Order;
import com.aviras.mrassistant.logger.Log;
import com.aviras.mrassistant.ui.lists.ListAdapter;
import com.aviras.mrassistant.ui.lists.ListFragment;

import io.realm.RealmResults;

/**
 * Show {@link Order}s list
 * <p/>
 * Created by ashish on 15/6/16.
 */
public class OrdersListFragment extends ListFragment<Order> implements OrdersList.OrdersListView {

    private static final String LOG_TAG = OrdersListFragment.class.getSimpleName();
    private ListAdapter mAdapter = new OrdersListAdapter();

    public static ListFragment newInstance(String listFor) {
        OrdersListFragment fragment = new OrdersListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LIST_FOR, listFor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public CharSequence getTitle(Context context) {
        return context.getString(R.string.title_activity_orders);
    }

    @Override
    public void setItems(RealmResults<Order> orders) {
        Log.v(LOG_TAG, "setItems - orders: " + orders);
        if (null != mAdapter) {
            mAdapter.setItems(orders);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected ListAdapter<OrdersListAdapter.ViewHolder, Order> getListAdapter(Context context) {
        Log.v(LOG_TAG, "getListAdapter");
        return mAdapter;
    }
}
