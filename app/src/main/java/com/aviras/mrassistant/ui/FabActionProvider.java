package com.aviras.mrassistant.ui;

import android.view.View;

/**
 * {@link android.support.v4.app.Fragment}s shown in {@link android.support.v4.view.ViewPager} will
 * implement it for providing fab clicked function.
 * <p/>
 * Created by ashish on 14/6/16.
 */
public interface FabActionProvider {
    void onFabClicked(View v);
}
