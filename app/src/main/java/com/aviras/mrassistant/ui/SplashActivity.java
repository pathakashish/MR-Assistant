package com.aviras.mrassistant.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.ui.editors.EditorActivity;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 2000;
    private static final String KEY_MOVE_TO_NEXT = "move_to_next";
    private boolean mMoveToNext = false;
    private boolean mIsForeground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != savedInstanceState) {
            mMoveToNext = savedInstanceState.getBoolean(KEY_MOVE_TO_NEXT);
        }
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsForeground = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsForeground = true;
        if (mMoveToNext) {
            moveToNextActivity();
        } else {
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMoveToNext = true;
                    if (isForeground() && !isDestroyed()) {
                        moveToNextActivity();
                    }
                }
            }, SPLASH_DELAY);
        }
    }

    private boolean isForeground() {
        return mIsForeground;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (null == outState) {
            outState = new Bundle();
            outState.putBoolean(KEY_MOVE_TO_NEXT, mMoveToNext);
        }
        super.onSaveInstanceState(outState);
    }

    private void moveToNextActivity() {
        Intent intent = new Intent(this, EditorActivity.class);
        intent.putExtra(EditorActivity.EXTRA_EDITING_FOR, EditorActivity.DOCTOR);
        startActivity(intent);
        finish();
    }
}
