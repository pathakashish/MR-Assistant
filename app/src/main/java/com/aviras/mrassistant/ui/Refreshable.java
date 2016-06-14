package com.aviras.mrassistant.ui;

import android.content.Context;

/**
 * {@link android.support.v4.app.Fragment}s shown in {@link android.support.v4.view.ViewPager} can
 * implement it so that they get callback to refresh data when that page is selected
 * <p/>
 * Created by ashish on 14/6/16.
 */
public interface Refreshable {
    void refresh(Context applicationContext);
}
