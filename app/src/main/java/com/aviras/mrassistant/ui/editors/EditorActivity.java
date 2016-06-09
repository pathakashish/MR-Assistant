package com.aviras.mrassistant.ui.editors;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.aviras.mrassistant.R;

public class EditorActivity extends AppCompatActivity
        implements EditorFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag(EditorFragment.TAG) == null) {
            EditorFragment fragment = new EditorFragment();
            fm.beginTransaction().add(R.id.container, fragment, EditorFragment.TAG).commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
