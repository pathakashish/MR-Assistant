package com.aviras.mrassistant.ui.editors;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.ui.utils.UiUtil;

public class EditorActivity extends AppCompatActivity
        implements EditorFragment.OnFragmentInteractionListener {

    public static final String EXTRA_EDITING_FOR = "editing_for";
    public static final String EXTRA_ID = "id";
    public static final String DOCTOR = "doctor";
    public static final String MEDICINE = "medicine";
    public static final String UNIT = "unit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String editingFor = getIntent().getStringExtra(EXTRA_EDITING_FOR);
        if (TextUtils.isEmpty(editingFor)) {
            UiUtil.showToast(this, R.string.no_editing_for_specified, true);
            finish();
            return;
        }

        setContentView(R.layout.activity_editor);

        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag(EditorFragment.TAG) == null) {
            int id = getIntent().getIntExtra(EXTRA_ID, 0);
            EditorFragment fragment = EditorFragment.newInstance(editingFor, id);
            fm.beginTransaction().add(R.id.container, fragment, EditorFragment.TAG).commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
