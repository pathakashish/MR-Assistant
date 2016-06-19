package com.aviras.mrassistant.ui.orders;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.Doctor;
import com.aviras.mrassistant.data.models.Medicine;
import com.aviras.mrassistant.data.models.Order;
import com.aviras.mrassistant.data.models.OrderItem;
import com.aviras.mrassistant.data.models.SupportedUnit;
import com.aviras.mrassistant.logger.Log;
import com.aviras.mrassistant.ui.DatePickerFragment;
import com.aviras.mrassistant.ui.InternalActivityStack;
import com.aviras.mrassistant.ui.Presenter;
import com.aviras.mrassistant.ui.SetValueLayoutHelper;
import com.aviras.mrassistant.ui.editors.EditorActivity;
import com.aviras.mrassistant.ui.editors.ListEditor;
import com.aviras.mrassistant.ui.utils.FtsUtil;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.realm.RealmList;

/**
 * Responsible for noting ordetr
 * <p/>
 * Created by ashish on 19/6/16.
 */
public class OrdersEditorAdapter extends ListEditor.ListEditorAdapter<OrdersEditorAdapter.ViewHolder> {

    private static final int TYPE_ORDER_ITEM = 5687987;
    private static final int TYPE_SUMMARY = 45387987;
    private static final int TYPE_ADD_MEDICINE = 56454;
    private static final String LOG_TAG = OrdersEditorAdapter.class.getSimpleName();
    static DecimalFormat decimalFormatter = new DecimalFormat("#.##");
    static SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

    private Order mOrder;
    private RealmList<Medicine> mItems;
    protected List<CharSequence> mFtsQueries;
    private CharSequence mSearchText;
    private int mFocusedPosition = RecyclerView.NO_POSITION;
    private RealmList<Doctor> mDoctors;

    public OrdersEditorAdapter(Order order) {
        if (null == order) {
            order = new Order();
            order.setCreatedDate(System.currentTimeMillis());
        }
//        Parcel parcel = Parcel.obtain();
//        order.writeToParcel(parcel, 0);
//        parcel.setDataPosition(0);
//        mOrder = Order.CREATOR.createFromParcel(parcel);
//        parcel.recycle();
        mOrder = new Order(order);
        Log.v(LOG_TAG, "mOrder: " + mOrder);
    }

    private RealmList<Doctor> getDoctors() {
        return mDoctors;
    }

    private void setFocusedPosition(int adapterPosition) {
        mFocusedPosition = adapterPosition;
    }

    private void setDoctor(Doctor item) {
        mOrder.setDoctor(item);
    }

    public void setItems(RealmList<Medicine> items) {
        mItems = items;
    }

    private void setExpectedDeliveryDate(long timeInMillis) {
        mOrder.setExpectedDeliveryDate(timeInMillis);
        notifyDataSetChanged();
    }

    private void setUnit(Medicine medicine, int position) {
        OrderItem orderItem = getAssociatedOrderItem(medicine);
        if (null != orderItem) {
            orderItem.setUnit(medicine.getSupportedUnits().get(position));
        }
        notifyDataSetChanged();
    }

    private float getTotal() {
        float total = 0;
        for (OrderItem item : mOrder.getItems()) {
            total += (item.getQuantity() * item.getUnit().getUnitPrice());
        }
        return total;
    }

    public void setFtsQueries(List<CharSequence> ftsQueries) {
        mFtsQueries = ftsQueries;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return TYPE_ADD_MEDICINE == viewType ? getEmptyViewHolder(parent, viewType)
                : getListItemViewHolder(parent, viewType);
    }

    protected ViewHolder getListItemViewHolder(ViewGroup parent, int viewType) {
        if (TYPE_SUMMARY == viewType) {
            return new SummaryViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_order_editor_list_item_summary, parent, false));
        } else if (TYPE_ORDER_ITEM == viewType) {
            final OrderItemViewHolder holder = new OrderItemViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_order_editor_list_order_item, parent, false));
            holder.quantityHelper.setOnValueChangeListener(new SetValueLayoutHelper.OnValueChangeListener() {
                @Override
                public void onValueChange(float newValue) {
                    OrderItem orderItem = getAssociatedOrderItem(mItems.get(holder.getAdapterPosition() - 1));
                    if (null != orderItem) {
                        orderItem.setQuantity(newValue);
                        holder.updateView(mFtsQueries);
                    }
                }
            });
            return holder;
        } else {
            throw new RuntimeException("");
        }
    }

    private void search(CharSequence text) {
        mSearchText = text;
        OrderEditor.sharedInstance().loadMedicines(text);
    }

    private void onItemSelected(int position, boolean isChecked) {
        Medicine medicine = mItems.get(position);
        if (isChecked) {
            OrderItem orderItem = new OrderItem();
            orderItem.setMedicine(medicine);
            for (SupportedUnit unit : medicine.getSupportedUnits()) {
                if (unit.isDefault()) {
                    orderItem.setUnit(unit);
                    break;
                }
            }
            orderItem.setQuantity(1);
            mOrder.getItems().add(orderItem);
        } else {
            for (OrderItem item : mOrder.getItems()) {
                if (item.getMedicine().getId() == medicine.getId()) {
                    mOrder.getItems().remove(item);
                    break;
                }
            }
        }

        notifyDataSetChanged();
    }

    private int getPositionInOrderItems(Medicine medicine) {
        int index = 0;
        for (OrderItem orderItem : mOrder.getItems()) {
            if (medicine.getId() == orderItem.getMedicine().getId()) {
                return index + 1;
            }
            index++;
        }
        return -1;
    }

    private OrderItem getAssociatedOrderItem(Medicine medicine) {
        for (OrderItem orderItem : mOrder.getItems()) {
            if (orderItem.getMedicine().getId() == medicine.getId()) {
                return orderItem;
            }
        }
        return null;
    }

    protected ViewHolder getEmptyViewHolder(ViewGroup parent, int viewType) {
        return new ButtonViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_add_button, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.adapter = this;
        if (null == mItems || mItems.isEmpty()) {
            return;
        }
        if (position > 0) {
            holder.medicine = mItems.get(position - 1);
        }
        holder.updateView(FtsUtil.getAllPossibleSearchStrings(null == mSearchText ? "" : mSearchText));
    }

    @Override
    public int getItemCount() {
        return null == mItems || mItems.isEmpty() ? 1 : mItems.size() + 1; // Account for one extra view for summary
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.isEmpty(mSearchText) && (null == mItems || mItems.isEmpty())) {
            return TYPE_ADD_MEDICINE;
        } else {
            if (0 == position) {
                return TYPE_SUMMARY;
            } else {
                return TYPE_ORDER_ITEM;
            }
        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.dispatchRefs();
    }

    public RealmList<OrderItem> getOrderItems() {
        return mOrder.getItems();
    }

    public Doctor getDoctor() {
        return mOrder.getDoctor();
    }

    public long getExpectedDeliveryDate() {
        return mOrder.getExpectedDeliveryDate();
    }

    public void setDoctors(RealmList<Doctor> doctors) {
        this.mDoctors = doctors;
    }

    public Order getOrder() {
        return mOrder;
    }

    public abstract static class ViewHolder extends RecyclerView.ViewHolder {

        boolean superClearAllRefsCalled;
        OrdersEditorAdapter adapter;
        public Medicine medicine;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        void dispatchRefs() {
            superClearAllRefsCalled = false;
            clearAllRefs();
            if (!superClearAllRefsCalled) {
                throw new RuntimeException("super.clearAllRefs() not called");
            }
        }

        abstract void updateView(List<CharSequence> mFtsQueries);

        void clearAllRefs() {
            superClearAllRefsCalled = true;
            adapter = null;
        }
    }

    public static class SummaryViewHolder extends ViewHolder implements TextWatcher, View.OnFocusChangeListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

        private static final String LOG_TAG = SummaryViewHolder.class.getSimpleName();
        AppCompatEditText searchEditText;
        AppCompatTextView totalTextView;
        AppCompatSpinner doctorsSpinner;
        AppCompatButton addDoctorButton;
        AppCompatButton selectDeliveryDateButton;
        DoctorsAdapter doctorsAdapter;

        public SummaryViewHolder(View itemView) {
            super(itemView);
            searchEditText = (AppCompatEditText) itemView.findViewById(R.id.search_edittext);
            addDoctorButton = (AppCompatButton) itemView.findViewById(R.id.add_doctor_button);
            addDoctorButton.setOnClickListener(this);
            selectDeliveryDateButton = (AppCompatButton) itemView.findViewById(R.id.select_delivery_date_button);
            selectDeliveryDateButton.setOnClickListener(this);
            doctorsAdapter = new DoctorsAdapter();
            doctorsSpinner = (AppCompatSpinner) itemView.findViewById(R.id.doctors_spinner);
            doctorsSpinner.setAdapter(doctorsAdapter);
            totalTextView = (AppCompatTextView) itemView.findViewById(R.id.total_textview);
        }

        @Override
        void updateView(List<CharSequence> ftsQueries) {
            searchEditText.removeTextChangedListener(this);
            searchEditText.setText(adapter.getSearchText());
            searchEditText.addTextChangedListener(this);
            searchEditText.setOnFocusChangeListener(this);
            if (adapter.mFocusedPosition == getAdapterPosition()) {
                searchEditText.requestFocus();
                searchEditText.setSelection(searchEditText.getText().length());
            }
            if (adapter.getExpectedDeliveryDate() == 0) {
                selectDeliveryDateButton.setText(R.string.select_delivery_date);
            } else {
                selectDeliveryDateButton.setText(df.format(adapter.getExpectedDeliveryDate()));
            }
            doctorsSpinner.setOnItemSelectedListener(null);
            doctorsAdapter.setItems(adapter.getDoctors());
            doctorsAdapter.notifyDataSetChanged();
            doctorsSpinner.setSelection(getDoctorPosition(adapter.getDoctor()));
            if (doctorsAdapter.getCount() > 0) {
                doctorsSpinner.setVisibility(View.VISIBLE);
                addDoctorButton.setVisibility(View.GONE);
            } else {
                doctorsSpinner.setVisibility(View.GONE);
                addDoctorButton.setVisibility(View.VISIBLE);
            }
            doctorsSpinner.setOnItemSelectedListener(this);
            totalTextView.setText(itemView.getContext().getString(R.string.total, decimalFormatter.format(adapter.getTotal())));
        }

        private int getDoctorPosition(Doctor doctor) {
            if (null != doctor) {
                for (int index = 0; index < doctorsAdapter.getCount(); index++) {
                    if (((Doctor) doctorsAdapter.getItem(index)).getId() == doctor.getId()) {
                        return index;
                    }
                }
            }
            return 0;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            adapter.search(s.toString());
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                adapter.setFocusedPosition(getAdapterPosition());
            }
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.add_doctor_button:
                    Intent intent = new Intent(v.getContext(), EditorActivity.class);
                    intent.putExtra(EditorActivity.EXTRA_EDITING_FOR, Presenter.DOCTOR);
                    v.getContext().startActivity(intent);
                    break;
                case R.id.select_delivery_date_button:
                    CharSequence dateText = selectDeliveryDateButton.getText();
                    long date;
                    try {
                        date = df.parse(dateText.toString()).getTime();
                    } catch (Exception e) {
                        date = System.currentTimeMillis();
                    }
                    FragmentActivity activity = InternalActivityStack.getActivityAtTop();
                    if (null != activity) {
                        new DatePickerFragment(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar cal = Calendar.getInstance();
                                cal.set(Calendar.YEAR, year);
                                cal.set(Calendar.MONTH, monthOfYear);
                                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                adapter.setExpectedDeliveryDate(cal.getTimeInMillis());
                                selectDeliveryDateButton.setText(df.format(cal.getTimeInMillis()));
                                adapter.notifyDataSetChanged();
                            }
                        }, date).show(activity.getSupportFragmentManager(), DatePickerFragment.TAG);
                    } else {
                        Log.w(LOG_TAG, "We shouldn't be here. How did this happen");
                    }
                    break;
            }
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            adapter.setDoctor((Doctor) doctorsAdapter.getItem(position));
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private CharSequence getSearchText() {
        return mSearchText;
    }

    public static class OrderItemViewHolder extends ViewHolder implements View.OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher, View.OnFocusChangeListener {

        AppCompatTextView nameTextView;
        AppCompatTextView descriptionTextView;
        AppCompatTextView priceTextView;
        AppCompatEditText noteEditText;
        AppCompatSpinner unitsSpinner;
        SetValueLayoutHelper quantityHelper;
        SupportedUnitsAdapter unitsAdapter;
        ForegroundColorSpan mColorAccent;

        public OrderItemViewHolder(View itemView) {
            super(itemView);
            mColorAccent = new ForegroundColorSpan(ContextCompat.getColor(itemView.getContext(), R.color.colorAccent));
            nameTextView = (AppCompatTextView) itemView.findViewById(R.id.name_textview);
            priceTextView = (AppCompatTextView) itemView.findViewById(R.id.price_textview);
            descriptionTextView = (AppCompatTextView) itemView.findViewById(R.id.description_textview);
            noteEditText = (AppCompatEditText) itemView.findViewById(R.id.note_edittext);
            noteEditText.addTextChangedListener(this);
            noteEditText.setOnFocusChangeListener(this);
            unitsSpinner = (AppCompatSpinner) itemView.findViewById(R.id.units_spinner);
            unitsSpinner.setOnItemSelectedListener(this);
            unitsAdapter = new SupportedUnitsAdapter();
            unitsSpinner.setAdapter(unitsAdapter);
            quantityHelper = new SetValueLayoutHelper(itemView);
            quantityHelper.setStepSize(1);
            itemView.setOnClickListener(this);
        }

        @Override
        void updateView(List<CharSequence> ftsQueries) {
            setMedicineDescription(ftsQueries);
            OrderItem orderItem = adapter.getAssociatedOrderItem(medicine);
            quantityHelper.pauseListeners();
            unitsSpinner.setOnItemSelectedListener(null);
            noteEditText.removeTextChangedListener(this);
            if (null == orderItem) {
                priceTextView.setVisibility(View.GONE);
                quantityHelper.setVisibility(View.GONE);
                unitsSpinner.setVisibility(View.GONE);
                noteEditText.setVisibility(View.GONE);
                unitsAdapter.setItems(null);
                unitsAdapter.notifyDataSetChanged();
                noteEditText.setText("");
            } else {
                priceTextView.setVisibility(View.VISIBLE);
                quantityHelper.setVisibility(View.VISIBLE);
                unitsSpinner.setVisibility(View.VISIBLE);
                noteEditText.setVisibility(View.VISIBLE);
                noteEditText.setText(orderItem.getNote());
                quantityHelper.setValue(orderItem.getQuantity());
                priceTextView.setText(itemView.getContext().getString(R.string.price, decimalFormatter.format(orderItem.getQuantity() * orderItem.getUnit().getUnitPrice())));
                unitsAdapter.setItems(medicine.getSupportedUnits());
                unitsAdapter.notifyDataSetChanged();
                unitsSpinner.setSelection(getPositionFor(orderItem.getUnit(), medicine));
            }
            quantityHelper.resumeListeners();
            unitsSpinner.setOnItemSelectedListener(this);
            noteEditText.addTextChangedListener(this);
            if (adapter.mFocusedPosition == getAdapterPosition()) {
                noteEditText.requestFocus();
                noteEditText.setSelection(noteEditText.getText().length());
            }
        }

        private int getPositionFor(SupportedUnit unit, Medicine medicine) {
            int position = 0;
            for (SupportedUnit su : medicine.getSupportedUnits()) {
                if (su.getUnit().getId() == unit.getUnit().getId()) {
                    return position;
                }
                position++;
            }
            return 0;
        }

        private void setMedicineDescription(List<CharSequence> ftsQueries) {
            String name = medicine.getName();
            String description = medicine.getDescription() == null ? "" : medicine.getDescription();
            if (null == ftsQueries || ftsQueries.isEmpty()) {
                nameTextView.setText(name);
                descriptionTextView.setText(description);
            } else {
                Spannable spannedName = new SpannableString(name);
                Spannable spannedDescription = new SpannableString(description);
                for (CharSequence query : ftsQueries) {
                    int nameSpanStartIndex = name.toLowerCase().indexOf(query.toString().toLowerCase());
                    if (nameSpanStartIndex >= 0) {
                        int nameSpanEndIndex = Math.min(nameSpanStartIndex + query.length(), nameSpanStartIndex + (name.length() - nameSpanStartIndex));
                        spannedName.setSpan(mColorAccent, nameSpanStartIndex, nameSpanEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    int descriptionSpanStartIndex = description.toLowerCase().indexOf(query.toString().toLowerCase());
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
            adapter.onItemSelected(getAdapterPosition() - 1, adapter.getAssociatedOrderItem(medicine) == null);
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            adapter.setUnit(medicine, position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            OrderItem orderItem = adapter.getAssociatedOrderItem(medicine);
            if (null != orderItem) {
                orderItem.setNote(s.toString());
            }
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                adapter.setFocusedPosition(getAdapterPosition());
            }
        }
    }

    public static class ButtonViewHolder extends ViewHolder implements View.OnClickListener {

        public ButtonViewHolder(View itemView) {
            super(itemView);
            AppCompatButton addButton = (AppCompatButton) itemView.findViewById(R.id.increase_button);
            addButton.setText(R.string.add_medicine);
            addButton.setOnClickListener(this);
        }

        @Override
        void updateView(List<CharSequence> mFtsQueries) {

        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), EditorActivity.class);
            intent.putExtra(EditorActivity.EXTRA_EDITING_FOR, Presenter.MEDICINE);
            v.getContext().startActivity(intent);
        }
    }

    private static class SupportedUnitsAdapter extends BaseAdapter {

        private RealmList<SupportedUnit> mItems;

        @Override
        public int getCount() {
            return null == mItems || mItems.isEmpty() ? 0 : mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_edit_mode_item, parent, false);

            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (null == holder) {
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            holder.nameTextView.setText(mItems.get(position).getUnit().getName());
            return convertView;
        }

        public void setItems(RealmList<SupportedUnit> supportedUnits) {
            mItems = supportedUnits;
        }

        private static class ViewHolder {
            AppCompatTextView nameTextView;

            ViewHolder(View itemView) {
                nameTextView = (AppCompatTextView) itemView.findViewById(R.id.name_textview);
                itemView.findViewById(R.id.delete_button).setVisibility(View.GONE);
            }
        }
    }

    private static class DoctorsAdapter extends BaseAdapter {

        private RealmList<Doctor> mItems;

        @Override
        public int getCount() {
            return null == mItems || mItems.isEmpty() ? 0 : mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_edit_mode_item, parent, false);

            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (null == holder) {
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            holder.nameTextView.setText(mItems.get(position).getName());
            return convertView;
        }

        public void setItems(RealmList<Doctor> supportedUnits) {
            mItems = supportedUnits;
        }

        private static class ViewHolder {
            AppCompatTextView nameTextView;

            ViewHolder(View itemView) {
                nameTextView = (AppCompatTextView) itemView.findViewById(R.id.name_textview);
                itemView.findViewById(R.id.delete_button).setVisibility(View.GONE);
            }
        }
    }
}
