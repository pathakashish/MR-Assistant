package com.aviras.mrassistant.ui;

import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;

import com.aviras.mrassistant.R;

import java.text.DecimalFormat;

/**
 * Helper for adjust price layout
 * <p/>
 * Created by ashish on 18/6/16.
 */
public class SetPriceLayoutHelper implements TextWatcher, View.OnTouchListener, Runnable {
    private static final long INITIAL_POST_DURATION = 250;
    private static final long REPEAT_DURATION = 21;
    private static final DecimalFormat mDf = new DecimalFormat("#.##");

    private View mLayout;
    private AppCompatImageButton mIncreaseButton;
    private AppCompatImageButton mDecreaseButton;

    private AppCompatEditText mPriceEditText;
    private float price;
    private float priceStep = 0.5f;
    private boolean increment;

    private OnPriceChangeListener mPriceChangeListener;

    public SetPriceLayoutHelper(View parent) {
        mLayout = parent.findViewById(R.id.price_layout);
        assert mLayout != null;
        mIncreaseButton = (AppCompatImageButton) mLayout.findViewById(R.id.increase_button);
        mDecreaseButton = (AppCompatImageButton) mLayout.findViewById(R.id.descrease_button);
        mPriceEditText = (AppCompatEditText) mLayout.findViewById(R.id.price_edittext);

        mPriceEditText.addTextChangedListener(this);
        mIncreaseButton.setOnTouchListener(this);
        mDecreaseButton.setOnTouchListener(this);
    }

    public float getPrice() {
        return price;
    }

    public void setVisibility(int visibility) {
        mLayout.setVisibility(visibility);
    }

    public void setPrice(float price) {
        this.price = price;
        mPriceEditText.setText(String.valueOf(mDf.format(price)));
    }

    public void setOnPriceChangeListener(OnPriceChangeListener mPriceChangeListener) {
        this.mPriceChangeListener = mPriceChangeListener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
//        if (s.length() == 0) {
//            mPriceEditText.append("0");
//            return;
//        }
        try {
            price = Float.parseFloat(s.toString());
            notifyListener();
        } catch (NullPointerException | NumberFormatException e) {
            price = 0.0f;
            notifyListener();
            // Do nothing
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        if (MotionEvent.ACTION_DOWN == action) {
            if (v == mIncreaseButton) {
                increment = true;
            } else if (v == mDecreaseButton) {
                increment = false;
            }
            updatePrice();
            mPriceEditText.postDelayed(this, INITIAL_POST_DURATION);
            v.setPressed(true);
        } else if (MotionEvent.ACTION_CANCEL == action
                || MotionEvent.ACTION_UP == action) {
            mPriceEditText.removeCallbacks(this);
            v.setPressed(false);
        }
        return true;
    }

    private void updatePrice() {
        if (increment) {
            price += priceStep;
        } else {
            if (price > priceStep) {
                price -= priceStep;
            }
        }
        mPriceEditText.setText(String.valueOf(mDf.format(price)));
        notifyListener();
    }

    private void notifyListener() {
        if (null != mPriceChangeListener) {
            mPriceChangeListener.onPriceChange(price);
        }
    }

    @Override
    public void run() {
        updatePrice();
        mPriceEditText.postDelayed(this, REPEAT_DURATION);
    }

    public void pauseListeners() {
        mPriceEditText.removeTextChangedListener(this);
    }

    public void resumeListeners() {
        mPriceEditText.addTextChangedListener(this);
    }

    public interface OnPriceChangeListener {
        void onPriceChange(float newPrice);
    }
}
