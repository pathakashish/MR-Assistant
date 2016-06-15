package com.aviras.mrassistant.ui.units;

import android.content.Intent;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.Unit;
import com.aviras.mrassistant.ui.Presenter;
import com.aviras.mrassistant.ui.editors.EditorActivity;
import com.aviras.mrassistant.ui.lists.ListAdapter;
import com.aviras.mrassistant.ui.lists.ListFragment;

import io.realm.RealmResults;

/**
 * Implementation for {@link com.aviras.mrassistant.ui.lists.ListAdapter} for {@link ListFragment}
 * <p/>
 * Created by ashish on 15/6/16.
 */
public class UnitsListAdapter extends ListAdapter<UnitsListAdapter.ViewHolder, Unit> {

    @Override
    public void setItems(RealmResults<Unit> items) {
        mItems = items;
    }

    @Override
    protected ViewHolder getListItemViewHolder(ViewGroup parent, int viewType) {
        return new ContentsViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_edit_mode_item, parent, false));
    }

    @Override
    protected ViewHolder getEmptyViewHolder(ViewGroup parent, int viewType) {
        return new EmptyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_empty_message, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (null == mItems || mItems.isEmpty()) {
            return;
        }
        holder.unit = mItems.get(position);
        holder.updateView(holder, position);
    }

    public abstract static class ViewHolder extends RecyclerView.ViewHolder {
        Unit unit;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        abstract void updateView(ViewHolder holder, int position);
    }

    public static class ContentsViewHolder extends ViewHolder implements View.OnClickListener {

        AppCompatTextView nameTextView;

        public ContentsViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            nameTextView = (AppCompatTextView) itemView.findViewById(R.id.name_textview);
            AppCompatImageButton deleteButton = (AppCompatImageButton) itemView.findViewById(R.id.delete_button);
            deleteButton.setOnClickListener(this);
        }

        @Override
        void updateView(ViewHolder holder, int position) {
            nameTextView.setText(unit.getName());
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.delete_button:
                    UnitsList.sharedInstance().delete(v.getContext(), unit);
                    break;
                default:
                    Intent intent = new Intent(v.getContext(), EditorActivity.class);
                    intent.putExtra(EditorActivity.EXTRA_EDITING_FOR, Presenter.UNIT);
                    intent.putExtra(EditorActivity.EXTRA_ID, unit.getId());
                    v.getContext().startActivity(intent);
                    break;
            }
        }
    }

    public static class EmptyViewHolder extends ViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
            AppCompatTextView emptyMessageTextView = (AppCompatTextView) itemView.findViewById(R.id.empty_message_textview);
            emptyMessageTextView.setText(R.string.empty_message_units);
        }

        @Override
        void updateView(ViewHolder holder, int position) {

        }
    }
}
