package com.aviras.mrassistant.ui.orders;

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
import com.aviras.mrassistant.data.models.Order;
import com.aviras.mrassistant.data.models.OrderItem;
import com.aviras.mrassistant.ui.Presenter;
import com.aviras.mrassistant.ui.editors.EditorActivity;
import com.aviras.mrassistant.ui.lists.ListAdapter;
import com.aviras.mrassistant.ui.lists.ListFragment;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.RealmResults;

/**
 * Implementation for {@link ListAdapter} for {@link ListFragment}
 * <p/>
 * Created by ashish on 15/6/16.
 */
public class OrdersListAdapter extends ListAdapter<OrdersListAdapter.ViewHolder, Order> {

    @Override
    public void setItems(RealmResults<Order> items) {
        mItems = items;
    }

    @Override
    protected ViewHolder getListItemViewHolder(ViewGroup parent, int viewType) {
        return new ContentsViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_order_list_item, parent, false));
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
        holder.order = mItems.get(position);
        holder.updateView(holder, position, mFtsQueries);
    }

    public static abstract class ViewHolder extends RecyclerView.ViewHolder {

        Order order;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        abstract void updateView(ViewHolder holder, int position, List<CharSequence> mFtsQueries);
    }

    public static class ContentsViewHolder extends ViewHolder implements View.OnClickListener {

        private final AppCompatImageButton doneButton;
        private final AppCompatImageButton deleteButton;
        AppCompatTextView nameTextView;
        AppCompatTextView descriptionTextView;
        AppCompatTextView datesInfoTextView;
        AppCompatTextView totalTextView;
        ForegroundColorSpan mColorAccent;
        static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault());
        private String notDelivered;
        private DecimalFormat decimalFormat = new DecimalFormat("#.##");

        public ContentsViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mColorAccent = new ForegroundColorSpan(ContextCompat.getColor(itemView.getContext(), R.color.colorAccent));
            nameTextView = (AppCompatTextView) itemView.findViewById(R.id.name_textview);
            descriptionTextView = (AppCompatTextView) itemView.findViewById(R.id.description_textview);
            datesInfoTextView = (AppCompatTextView) itemView.findViewById(R.id.dates_info_textview);
            datesInfoTextView.setLines(3);
            datesInfoTextView.setMaxLines(3);
            totalTextView = (AppCompatTextView) itemView.findViewById(R.id.total_textvview);
            totalTextView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorAccent));
            doneButton = (AppCompatImageButton) itemView.findViewById(R.id.delivered_button);
            doneButton.setOnClickListener(this);
            deleteButton = (AppCompatImageButton) itemView.findViewById(R.id.delete_button);
            deleteButton.setOnClickListener(this);
            notDelivered = itemView.getContext().getString(R.string.not_delivered);
        }

        @Override
        void updateView(ViewHolder holder, int position, List<CharSequence> ftsQueries) {
            String name = order.getDoctor().getName();
            String description = order.getSpecialRequest() == null ? "" : order.getSpecialRequest();
            String dates = itemView.getContext().getString(R.string.order_description,
                    dateFormat.format(new Date(order.getCreatedDate())),
                    dateFormat.format(new Date(order.getExpectedDeliveryDate())),
                    order.getActualDeliveryDate() == 0 ? notDelivered : dateFormat.format(order.getActualDeliveryDate()));
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

            datesInfoTextView.setText(dates);

            if (TextUtils.isEmpty(descriptionTextView.getText())) {
                descriptionTextView.setVisibility(View.GONE);
            } else {
                descriptionTextView.setVisibility(View.VISIBLE);
            }

            if (order.getActualDeliveryDate() == 0) {
                doneButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.GONE);
            } else {
                doneButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.VISIBLE);
            }
            float totalPrice = 0.0f;
            for (OrderItem item : order.getItems()) {
                totalPrice += (item.getQuantity() * item.getUnit().getUnitPrice());
            }
            totalTextView.setText(itemView.getContext().getString(R.string.price, decimalFormat.format(totalPrice)));
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.delete_button:
                    OrdersList.sharedInstance().delete(v.getContext(), order);
                    break;
                case R.id.delivered_button:
                    OrdersList.sharedInstance().deliver(v.getContext(), order);
                    doneButton.setVisibility(View.GONE);
                    deleteButton.setVisibility(View.VISIBLE);
                    break;
                default:
                    Intent intent = new Intent(v.getContext(), EditorActivity.class);
                    intent.putExtra(EditorActivity.EXTRA_EDITING_FOR, Presenter.ORDER);
                    intent.putExtra(EditorActivity.EXTRA_ID, order.getId());
                    v.getContext().startActivity(intent);
                    break;
            }
        }
    }

    public static class EmptyViewHolder extends ViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
            AppCompatTextView emptyMessageTextView = (AppCompatTextView) itemView.findViewById(R.id.empty_message_textview);
            emptyMessageTextView.setText(R.string.empty_message_orders);
        }

        @Override
        void updateView(ViewHolder holder, int position, List<CharSequence> mFtsQueries) {

        }
    }
}
