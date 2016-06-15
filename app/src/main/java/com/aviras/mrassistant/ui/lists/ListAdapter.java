package com.aviras.mrassistant.ui.lists;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by ashish on 15/6/16.
 */
public abstract class ListAdapter<VH extends RecyclerView.ViewHolder, T extends RealmObject>
        extends RecyclerView.Adapter<VH> {
    private static final int TYPE_EMPTY_VIEW = 125463;
    private static final int TYPE_LIST_ITEM = 546656;
    protected RealmResults<T> mItems;

    public abstract void setItems(RealmResults<T> items);

    @Override
    public int getItemCount() {
        // We will show empty message or view if we have nothing to show
        return null == mItems || mItems.isEmpty() ? 1 : mItems.size();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return TYPE_EMPTY_VIEW == viewType ? getEmptyViewHolder(parent, viewType)
                : getListItemViewHolder(parent, viewType);
    }

    protected abstract VH getListItemViewHolder(ViewGroup parent, int viewType);

    protected abstract VH getEmptyViewHolder(ViewGroup parent, int viewType);

    @Override
    public int getItemViewType(int position) {
        return null == mItems || mItems.isEmpty() ? TYPE_EMPTY_VIEW : TYPE_LIST_ITEM;
    }
}