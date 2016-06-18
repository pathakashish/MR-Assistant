package com.aviras.mrassistant.ui.medicines;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.SupportedUnit;
import com.aviras.mrassistant.data.models.Unit;
import com.aviras.mrassistant.ui.Presenter;
import com.aviras.mrassistant.ui.SetPriceLayoutHelper;
import com.aviras.mrassistant.ui.editors.EditorActivity;
import com.aviras.mrassistant.ui.editors.ListEditor;

import io.realm.RealmList;

/**
 * Show units list for either selecting unit or for editing unit
 * <p/>
 * Created by ashish on 11/6/16.
 */
public class UnitsAdapter extends ListEditor.ListEditorAdapter<UnitsAdapter.ViewHolder> {

    private static final int TYPE_ADD_BUTTON = 35146854;
    private static final int TYPE_SELECT = 547587;
    private RealmList<SupportedUnit> mSupportedUnits = new RealmList<>();

    public RealmList<SupportedUnit> getSelectedUnits() {
        return mSupportedUnits;
    }

    public void setSupportedUnits(@NonNull RealmList<SupportedUnit> supportedUnits) {
        mSupportedUnits.clear();
        for (SupportedUnit supportedUnit : supportedUnits) {
            mSupportedUnits.add(new SupportedUnit(supportedUnit));
        }
    }

    private RealmList<Unit> mUnits;

    public UnitsAdapter() {
    }

    public void setUnits(RealmList<Unit> units) {
        mUnits = units;
    }

    private void onUnitMarkedAsDefault(int position) {
        for (SupportedUnit supportedUnit : mSupportedUnits) {
            supportedUnit.setDefault(false);
        }
        SupportedUnit supportedUnit = getAssociatedSupportedUnits(mUnits.get(position));
        if (null != supportedUnit) {
            supportedUnit.setDefault(true);
        }
        notifyDataSetChanged();
    }

    private void onUnitSelected(int position, boolean isChecked) {
        Unit unit = mUnits.get(position);
        if (isChecked) {
            SupportedUnit supportedUnit = new SupportedUnit();
            supportedUnit.setUnit(mUnits.get(position));
            if (mSupportedUnits.isEmpty()) {
                supportedUnit.setDefault(true);
            }
            supportedUnit.setUnitPrice(1.0f);
            mSupportedUnits.add(supportedUnit);
        } else {
            int unitPositionInSupportedUnits = getPositionInSupportedUnits(unit);
            mSupportedUnits.remove(unitPositionInSupportedUnits);
            if (!mSupportedUnits.isEmpty()) {
                mSupportedUnits.get(0).setDefault(true);
            }
        }

        notifyDataSetChanged();
    }

    private int getPositionInSupportedUnits(Unit unit) {
        int index = 0;
        for (SupportedUnit supportedUnit : mSupportedUnits) {
            if (unit.getId() == supportedUnit.getUnit().getId()) {
                return index;
            }
            index++;
        }
        return -1;
    }

    private SupportedUnit getAssociatedSupportedUnits(Unit unit) {
        for (SupportedUnit supportedUnit : mSupportedUnits) {
            if (unit.getId() == supportedUnit.getUnit().getId()) {
                return supportedUnit;
            }
        }
        return null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder;
        if (TYPE_SELECT == viewType) {
            holder = new SelectViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_select_unit_item, parent, false));
            final SelectViewHolder selectViewHolder = (SelectViewHolder) holder;
            selectViewHolder.priceHelper.setOnPriceChangeListener(new SetPriceLayoutHelper.OnPriceChangeListener() {
                @Override
                public void onPriceChange(float newPrice) {
                    SupportedUnit supportedUnit = getAssociatedSupportedUnits(mUnits.get(selectViewHolder.getAdapterPosition()));
                    if (null != supportedUnit) {
                        supportedUnit.setUnitPrice(newPrice);
                    }
                }
            });
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
        holder.unitsAdapter = this;
        holder.selectCheckBox.setOnCheckedChangeListener(null);
        holder.selectCheckBox.setChecked(getPositionInSupportedUnits(holder.unit) >= 0);
        holder.selectCheckBox.setOnCheckedChangeListener(holder);
        holder.defaultRadioButton.setOnCheckedChangeListener(holder);

        holder.defaultRadioButton.setOnCheckedChangeListener(null);
        SupportedUnit supportedUnit = getAssociatedSupportedUnits(holder.unit);
        if (null == supportedUnit) {
            holder.defaultRadioButton.setVisibility(View.GONE);
            holder.priceHelper.setVisibility(View.GONE);
        } else {
            holder.defaultRadioButton.setChecked(supportedUnit.isDefault());
            holder.defaultRadioButton.setVisibility(View.VISIBLE);
            holder.priceHelper.setVisibility(View.VISIBLE);
            holder.priceHelper.pauseListeners();
            holder.priceHelper.setPrice(supportedUnit.getUnitPrice());
            holder.priceHelper.resumeListeners();
        }
        holder.defaultRadioButton.setOnCheckedChangeListener(holder);
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
            AppCompatButton addButton = (AppCompatButton) itemView.findViewById(R.id.increase_button);
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

        UnitsAdapter unitsAdapter;
        AppCompatCheckBox selectCheckBox;
        AppCompatRadioButton defaultRadioButton;
        SetPriceLayoutHelper priceHelper;

        public SelectViewHolder(View itemView) {
            super(itemView);
            priceHelper = new SetPriceLayoutHelper(itemView);
            selectCheckBox = (AppCompatCheckBox) itemView.findViewById(R.id.select_checkbox);
            defaultRadioButton = (AppCompatRadioButton) itemView.findViewById(R.id.default_radiobutton);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView == selectCheckBox && null != unitsAdapter) {
                unitsAdapter.onUnitSelected(getAdapterPosition(), isChecked);
            }

            if (buttonView == defaultRadioButton && null != unitsAdapter && isChecked) {
                unitsAdapter.onUnitMarkedAsDefault(getAdapterPosition());
            }
        }

        @Override
        void clearAllRefs() {
            super.clearAllRefs();
            unitsAdapter = null;
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                selectCheckBox.toggle();
            }
        }
    }
}
