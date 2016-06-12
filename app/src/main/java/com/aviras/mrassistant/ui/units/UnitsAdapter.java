package com.aviras.mrassistant.ui.units;

import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.Unit;
import com.aviras.mrassistant.ui.OnItemSelectedListener;
import com.aviras.mrassistant.ui.editors.EditorActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.realm.RealmList;

/**
 * Show units list for either selecting unit or for editing unit
 * <p/>
 * Created by ashish on 11/6/16.
 */
public class UnitsAdapter extends RecyclerView.Adapter<UnitsAdapter.ViewHolder>
        implements OnItemSelectedListener {

    public static final int MODE_SELECTION = 1;
    public static final int MODE_EDIT = 2;
    private RealmList<Unit> mSupportedUnits = new RealmList<>();

    public RealmList<Unit> getSelectedUnits() {
        return mSupportedUnits;
    }

    public void setSupportedUnits(@NonNull RealmList<Unit> supportedUnits) {
        mSupportedUnits.clear();
        this.mSupportedUnits.addAll(supportedUnits);
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MODE_SELECTION, MODE_EDIT})
    public @interface Mode {
    }

    @Mode
    private int mMode;
    private RealmList<Unit> mUnits;

    public UnitsAdapter(@Mode int selectionMode) {
        mMode = selectionMode;
    }

    public void setUnits(RealmList<Unit> supportedUnits) {
        mUnits = supportedUnits;
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
        if (MODE_EDIT == mMode) {
            holder = new EditViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_edit_mode_item, parent, false));
        } else if (MODE_SELECTION == mMode) {
            holder = new SelectViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_select_mode_item, parent, false));
        } else {
            throw new IllegalStateException("Unknown mode " + mMode);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.unit = mUnits.get(position);
        holder.nameTextView.setText(holder.unit.getName());

        if (MODE_EDIT == mMode) {
            onBindViewHolder((EditViewHolder) holder);
        } else if (MODE_SELECTION == mMode) {
            onBindViewHolder((SelectViewHolder) holder);
        }
    }

    public void onBindViewHolder(EditViewHolder holder) {
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
        return null == mUnits ? 0 : mUnits.size();
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
                selectRadioButton.setChecked(true);
            }
        }
    }

    public static class EditViewHolder extends ViewHolder implements View.OnClickListener {

        AppCompatImageButton deleteButton;

        public EditViewHolder(View itemView) {
            super(itemView);
            deleteButton = (AppCompatImageButton) itemView.findViewById(R.id.delete_button);
            deleteButton.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.delete_button:
                    break;
                default:
                    Intent intent = new Intent(v.getContext(), EditorActivity.class);
                    intent.putExtra(EditorActivity.EXTRA_EDITING_FOR, EditorActivity.UNIT);
                    intent.putExtra(EditorActivity.EXTRA_ID, 1);
                    v.getContext().startActivity(intent);
                    break;
            }
        }
    }
}
