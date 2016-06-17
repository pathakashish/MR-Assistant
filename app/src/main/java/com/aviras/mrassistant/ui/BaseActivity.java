package com.aviras.mrassistant.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Base class for activities in this project. All Activities in this project must extend this so
 * that app can manage them better
 * <p/>
 * Created by ashish on 3/2/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private final String LOG_TAG = this.getClass().getSimpleName();

    private boolean mIsForeground;

    public BaseActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.v(LOG_TAG, "onCreate");
        InternalActivityStack.pushActivity(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(LOG_TAG, "onStart");
        mIsForeground = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(LOG_TAG, "onStop");
        mIsForeground = false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(LOG_TAG, "onSaveInstanceState - outState: " + outState);
        // From Android documentation at: http://developer.android.com/reference/android/app/Activity.html
        //
        // Starting with Honeycomb, an application is not in the killable state until its onStop() has
        // returned. This impacts when onSaveInstanceState(Bundle) may be called (it may be safely
        // called after onPause() and allows and application to safely wait until onStop() to save
        // persistent state.

        // Because of this, we need to set mIsForeground to false in both onStop and onSaveInstanceState
        // and set it to true in both onStart and onResume. This way we cover cases where onSaveInstanceState
        // is called and onSaveInstanceState is not called. Because we get IllegalStateException if we
        // commit fragment transaction after onStop or onSaveInstanceState(in case it is called),we
        // should use this flag to determine if we should commit any fragment transaction.
        mIsForeground = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "onResume");
        mIsForeground = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(LOG_TAG, "onPause");
    }

    protected boolean isInForeground() {
        return mIsForeground;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(LOG_TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "onDestroy");
        InternalActivityStack.removeActivity(this);
    }
}
