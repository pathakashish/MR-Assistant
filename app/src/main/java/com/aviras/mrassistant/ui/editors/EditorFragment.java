package com.aviras.mrassistant.ui.editors;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.logger.Log;
import com.aviras.mrassistant.ui.Presenter;
import com.aviras.mrassistant.ui.doctors.DoctorEditor;
import com.aviras.mrassistant.ui.medicines.MedicineEditor;
import com.aviras.mrassistant.ui.orders.OrderEditor;
import com.aviras.mrassistant.ui.units.UnitEditor;
import com.aviras.mrassistant.ui.utils.UiUtil;

import java.util.List;

/**
 * Editor which hosts the {@link RecyclerView} for showing {@link Editor} entries
 */
public class EditorFragment extends Fragment implements EditorView {

    public static final String TAG = "EditorFragment";
    private String LOG_TAG = "EditorFragment";
    private static final String KEY_PRESENTER_STATE = "presenter_state";

    private OnFragmentInteractionListener mListener;
    private EditorPresenter mPresenter;
    private EditorAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private int mId;

    public static EditorFragment newInstance(String editingFor, int id) {
        EditorFragment fragment = new EditorFragment();
        Bundle args = new Bundle();
        args.putString(EditorActivity.EXTRA_EDITING_FOR, editingFor);
        args.putInt(EditorActivity.EXTRA_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    public EditorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate - savedInstanceState: " + savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            String editingFor = getArguments().getString(EditorActivity.EXTRA_EDITING_FOR);
            LOG_TAG += editingFor;
            if (Presenter.UNIT.equals(editingFor)) {
                mPresenter = UnitEditor.sharedInstance();
            } else if (Presenter.MEDICINE.equals(editingFor)) {
                mPresenter = MedicineEditor.sharedInstance();
            } else if (Presenter.DOCTOR.equals(editingFor)) {
                mPresenter = DoctorEditor.sharedInstance();
            } else if (Presenter.ORDER.equals(editingFor)) {
                mPresenter = OrderEditor.sharedInstance();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "onDestroy");
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView - savedInstanceState: " + savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_editor, container, false);
        initToolbar(view);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mLayoutManager = new LinearLayoutManager(inflater.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new EditorAdapter();
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);

        mId = getArguments().getInt(EditorActivity.EXTRA_ID, 0);

        mPresenter.setView(this);

        if (null != savedInstanceState) {
            mPresenter.setState(inflater.getContext(), savedInstanceState.getBundle(KEY_PRESENTER_STATE));
        } else {
            mPresenter.load(inflater.getContext(), mId);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.v(LOG_TAG, "onSaveInstanceState: " + outState);
        if (null == outState) {
            outState = new Bundle();
        }
        Bundle state = mPresenter.getState(mAdapter.getEditors(), mId);
        outState.putBundle(KEY_PRESENTER_STATE, state);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        Log.v(LOG_TAG, "onDestroyView");
        super.onDestroyView();
        mPresenter.setView(null);
    }

    @Override
    public void showEditors(List<Editor> editors) {
        Log.v(LOG_TAG, "showEditors - editors: " + editors);
        mAdapter.setEditors(editors);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public List<Editor> getEditors() {
        return null == mAdapter ? null : mAdapter.getEditors();
    }

    private void initToolbar(View view) {
        if (getActivity() == null) {
            Log.w(LOG_TAG, "initToolbar - not attached to activity");
            return;
        }
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        if (null != toolbar) {
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.setTitle(mPresenter.getTitle(view.getContext()));
            toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.v(LOG_TAG, "onAttach");
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v(LOG_TAG, "onDetach");
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.v(LOG_TAG, "onCreateOptionsMenu - menu: " + menu);
        inflater.inflate(R.menu.menu_done, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(LOG_TAG, "onOptionsItemSelected - item: " + item);
        switch (item.getItemId()) {
            case R.id.action_menu_done:
                for (Editor editor : mAdapter.getEditors()) {
                    if (null != editor.getValidator() && !editor.getValidator().validate()) {
                        UiUtil.showToast(getActivity(), R.string.please_correct_errors, true);
                        return false;
                    }
                }
                mPresenter.saveOrUpdateObject(mAdapter.getEditors(), mId);
                getActivity().finish();
                return true;
        }
        return false;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
