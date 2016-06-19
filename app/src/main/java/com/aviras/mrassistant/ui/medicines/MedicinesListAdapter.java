package com.aviras.mrassistant.ui.medicines;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.Medicine;
import com.aviras.mrassistant.ui.Presenter;
import com.aviras.mrassistant.ui.editors.EditorActivity;
import com.aviras.mrassistant.ui.lists.ListAdapter;
import com.aviras.mrassistant.ui.lists.ListFragment;

import java.util.List;

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
        return new ContentsViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_edit_mode_two_line_item, parent, false));
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
        holder.medicine = mItems.get(position);
        holder.updateView(mFtsQueries);
    }

    public static abstract class ViewHolder extends RecyclerView.ViewHolder {

        Medicine medicine;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        abstract void updateView(List<CharSequence> mFtsQueries);
    }

    public static class ContentsViewHolder extends ViewHolder implements View.OnClickListener {

        AppCompatTextView nameTextView;
        AppCompatTextView descriptionTextView;
        ForegroundColorSpan mColorAccent;

        public ContentsViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mColorAccent = new ForegroundColorSpan(ContextCompat.getColor(itemView.getContext(), R.color.colorAccent));
            nameTextView = (AppCompatTextView) itemView.findViewById(R.id.name_textview);
            descriptionTextView = (AppCompatTextView) itemView.findViewById(R.id.description_textview);
            AppCompatImageButton deleteButton = (AppCompatImageButton) itemView.findViewById(R.id.delete_button);
            deleteButton.setOnClickListener(this);
        }

        @Override
        void updateView(List<CharSequence> ftsQueries) {
            String name = medicine.getName();
            String description = medicine.getDescription() == null ? "" : medicine.getDescription();
            if (null == ftsQueries || ftsQueries.isEmpty()) {
                nameTextView.setText(name);
                descriptionTextView.setText(description);
            } else {
                Spannable spannedName = new SpannableString(name);
                Spannable spannedDescription = new SpannableString(description);
                for (CharSequence query : ftsQueries) {
                    int nameSpanStartIndex = name.indexOf(query.toString());
                    if (nameSpanStartIndex >= 0) {
                        int nameSpanEndIndex = Math.min(nameSpanStartIndex + query.length(), nameSpanStartIndex + (name.length() - nameSpanStartIndex));
                        spannedName.setSpan(mColorAccent, nameSpanStartIndex, nameSpanEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    int descriptionSpanStartIndex = description.indexOf(query.toString());
                    if (descriptionSpanStartIndex >= 0) {
                        int descriptionSpanEndIndex = Math.min(descriptionSpanStartIndex + query.length(), descriptionSpanStartIndex + (description.length() - descriptionSpanStartIndex));
                        spannedDescription.setSpan(mColorAccent, descriptionSpanStartIndex, descriptionSpanEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
                nameTextView.setText(spannedName);
                descriptionTextView.setText(spannedDescription);
            }
            if (TextUtils.isEmpty(descriptionTextView.getText())) {
                descriptionTextView.setVisibility(View.GONE);
            } else {
                descriptionTextView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.delete_button:
                    MedicinesList.sharedInstance().delete(v.getContext(), medicine);
                    break;
                default:
                    Intent intent = new Intent(v.getContext(), EditorActivity.class);
                    intent.putExtra(EditorActivity.EXTRA_EDITING_FOR, Presenter.MEDICINE);
                    intent.putExtra(EditorActivity.EXTRA_ID, medicine.getId());
                    v.getContext().startActivity(intent);
                    break;
            }
        }
    }

    public static class EmptyViewHolder extends ViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
            AppCompatTextView emptyMessageTextView = (AppCompatTextView) itemView.findViewById(R.id.empty_message_textview);
            emptyMessageTextView.setText(R.string.empty_message_medicines);
        }

        @Override
        void updateView(List<CharSequence> mFtsQueries) {

        }
    }
}
