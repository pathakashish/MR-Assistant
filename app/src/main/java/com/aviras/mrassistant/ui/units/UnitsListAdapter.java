package com.aviras.mrassistant.ui.units;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.aviras.mrassistant.data.models.Unit;
import com.aviras.mrassistant.ui.lists.ListFragment;

/**
 * Implementation for {@link ListFragment.ListAdapter} for {@link ListFragment}
 * <p/>
 * Created by ashish on 15/6/16.
 */
public class UnitsListAdapter extends ListFragment.ListAdapter<UnitsListAdapter.ViewHolder, Unit> {

    @Override
    protected ViewHolder getListItemViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    protected ViewHolder getEmptyViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
