package com.aviras.mrassistant.ui.medicines;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.Medicine;
import com.aviras.mrassistant.ui.lists.ListAdapter;
import com.aviras.mrassistant.ui.lists.ListFragment;

import io.realm.RealmResults;

/**
 * Implementation for {@link com.aviras.mrassistant.ui.lists.ListAdapter} for {@link ListFragment}
 * <p/>
 * Created by ashish on 15/6/16.
 */
public class MedicinesListAdapter extends ListAdapter<MedicinesListAdapter.ViewHolder, Medicine> {

    @Override
    public void setItems(RealmResults<Medicine> items) {
        mItems = items;
    }

    @Override
    protected ViewHolder getListItemViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    protected ViewHolder getEmptyViewHolder(ViewGroup parent, int viewType) {
        return new EmptyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_empty_message, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class EmptyViewHolder extends ViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
            AppCompatTextView emptyMessageTextView = (AppCompatTextView) itemView.findViewById(R.id.empty_message_textview);
            emptyMessageTextView.setText(R.string.empty_message_medicines);
        }
    }
}
