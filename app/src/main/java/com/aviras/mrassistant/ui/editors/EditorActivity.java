package com.aviras.mrassistant.ui.editors;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.aviras.mrassistant.R;

public class EditorActivity extends AppCompatActivity
        implements EditorFragment.OnFragmentInteractionListener {

    public static final String EXTRA_EDITING_FOR = "editing_for";
    public static final String DOCTOR = "doctor";
    public static final String MEDICINE = "medicine";
    public static final String UNIT = "unit";

    private EditorPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String editingFor = getIntent().getStringExtra(EXTRA_EDITING_FOR);
        if (TextUtils.isEmpty(editingFor)) {
            Toast.makeText(this, R.string.no_editing_for_specified, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (EditorActivity.UNIT.equals(editingFor)) {
            mPresenter = UnitPresenter.sharedInstance();
        } else if (EditorActivity.MEDICINE.equals(editingFor)) {
            mPresenter = MedicinePresenter.sharedInstance();
        } else if (EditorActivity.DOCTOR.equals(editingFor)) {
            mPresenter = DoctorPresenter.sharedInstance();
        }

        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (null != toolbar) {
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.setTitle(mPresenter.getTitle(this));
            toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag(EditorFragment.TAG) == null) {
            EditorFragment fragment = EditorFragment.newInstance(editingFor);
            fm.beginTransaction().add(R.id.container, fragment, EditorFragment.TAG).commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu_done:
//                mPresenter.save();
                finish();
                return true;
        }
        return false;
    }
}
