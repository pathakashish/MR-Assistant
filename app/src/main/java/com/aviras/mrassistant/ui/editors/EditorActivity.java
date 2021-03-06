package com.aviras.mrassistant.ui.editors;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.ui.BaseActivity;
import com.aviras.mrassistant.ui.utils.UiUtil;

public class EditorActivity extends BaseActivity
        implements EditorFragment.OnFragmentInteractionListener {

    public static final String EXTRA_EDITING_FOR = "editing_for";
    public static final String EXTRA_ID = "id";

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
