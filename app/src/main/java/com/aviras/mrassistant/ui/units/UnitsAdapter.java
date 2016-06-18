package com.aviras.mrassistant.ui.units;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.Unit;
import com.aviras.mrassistant.ui.OnItemSelectedListener;
import com.aviras.mrassistant.ui.Presenter;
import com.aviras.mrassistant.ui.editors.EditorActivity;
import com.aviras.mrassistant.ui.editors.ListEditor;

import io.realm.RealmList;

/**
 * Show units list for either selecting unit or for editing unit
 * <p/>
 * Created by ashish on 11/6/16.
 */
public class UnitsAdapter extends ListEditor.ListEditorAdapter<UnitsAdapter.ViewHolder>
        implements OnItemSelectedListener {

    private static final int TYPE_ADD_BUTTON = 35146854;
    private static final int TYPE_SELECT = 547587;
    private RealmList<Unit> mSupportedUnits = new RealmList<>();

    public RealmList<Unit> getSelectedUnits() {
        return mSupportedUnits;
    }

    public void setSupportedUnits(@NonNull RealmList<Unit> supportedUnits) {
        mSupportedUnits.clear();
        this.mSupportedUnits.addAll(supportedUnits);
    }

    private RealmList<Unit> mUnits;

    public UnitsAdapter() {
    }

    public void setUnits(RealmList<Unit> units) {
        mUnits = units;
    }

    @Override
    public void onItemSelected(int position, boolean isChecked) {
        Unit unit = mUnits.get(position);
        if (isChecked) {
            mSupportedUnits.add(unit);
        } else {
            int unitPositionInSupportedUnits = getPositionInSupportedUnits(unit);
            mSupportedUnits.remove(unitPositionInSupportedUnits);
        }

        notifyItemChanged(position);
    }

    private int getPositionInSupportedUnits(Unit unit) {
        int index = 0;
        for (Unit supportedUnit : mSupportedUnits) {
            if (unit.getId() == supportedUnit.getId()) {
                return index;
            }
            index++;
        }
        return -1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder;
        if (TYPE_SELECT == viewType) {
            holder = new SelectViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_select_mode_item, parent, false));
        } else {
            holder = new ButtonViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_add_button, parent, false));
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (null == mUnits || mUnits.isEmpty()) {
            return;
        }
        holder.unit = mUnits.get(position);
        holder.nameTextView.setText(holder.unit.getName());
        onBindViewHolder((SelectViewHolder) holder);
    }

    public void onBindViewHolder(SelectViewHolder holder) {
        holder.selectionChangedListener = this;
        holder.selectRadioButton.setOnCheckedChangeListener(null);
        holder.selectRadioButton.setChecked(getPositionInSupportedUnits(holder.unit) >= 0);
        holder.selectRadioButton.setOnCheckedChangeListener(holder);
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.clearAllRefs();
    }

    @Override
    public int getItemCount() {
        return null == mUnits || mUnits.size() == 0 ? 1 : mUnits.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (null == mUnits || mUnits.isEmpty()) {
            return TYPE_ADD_BUTTON;
        } else {
            return TYPE_SELECT;
        }
    }

    public abstract static class ViewHolder extends RecyclerView.ViewHolder {

        Unit unit;
        AppCompatTextView nameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (AppCompatTextView) itemView.findViewById(R.id.name_textview);
        }

        void clearAllRefs() {
        }
    }

    public static class ButtonViewHolder extends ViewHolder implements View.OnClickListener {

        public ButtonViewHolder(View itemView) {
            super(itemView);
            AppCompatButton addButton = (AppCompatButton) itemView.findViewById(R.id.add_button);
            addButton.setText(R.string.add_unit);
            addButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), EditorActivity.class);
            intent.putExtra(EditorActivity.EXTRA_EDITING_FOR, Presenter.UNIT);
            v.getContext().startActivity(intent);
        }
    }

    public static class SelectViewHolder extends ViewHolder implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

        OnItemSelectedListener selectionChangedListener;
        AppCompatCheckBox selectRadioButton;

        public SelectViewHolder(View itemView) {
            super(itemView);
            selectRadioButton = (AppCompatCheckBox) itemView.findViewById(R.id.select_checkbox);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (null != selectionChangedListener) {
                selectionChangedListener.onItemSelected(getAdapterPosition(), isChecked);
            }
        }

        @Override
        void clearAllRefs() {
            super.clearAllRefs();
            selectionChangedListener = null;
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                selectRadioButton.toggle();
            }
        }
    }
}
