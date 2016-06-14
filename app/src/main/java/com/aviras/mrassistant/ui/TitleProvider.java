package com.aviras.mrassistant.ui;

import android.content.Context;

/**
 * For providing titles where required. eg. to {@link android.support.v4.view.ViewPager}
 * <p/>
 * Created by ashish on 14/6/16.
 */
public interface TitleProvider {
    CharSequence getTitle(Context context);
}
