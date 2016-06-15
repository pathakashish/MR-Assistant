package com.aviras.mrassistant.ui.lists;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.ui.FabActionProvider;
import com.aviras.mrassistant.ui.Presenter;
import com.aviras.mrassistant.ui.Refreshable;
import com.aviras.mrassistant.ui.TitleProvider;
import com.aviras.mrassistant.ui.doctors.DoctorsList;
import com.aviras.mrassistant.ui.medicines.MedicinesList;
import com.aviras.mrassistant.ui.units.UnitsList;
import com.aviras.mrassistant.ui.utils.FtsUtil;

import io.realm.RealmObject;

/**
 * Shows list of {@link RealmObject}
 */
public abstract class ListFragment<T extends RealmObject> extends Fragment implements ListView<T>, TitleProvider, Refreshable, FabActionProvider {

    protected static final String ARG_LIST_FOR = "list_for";
    private static final String KEY_SEARCH_STRING = "search_string";

    private OnFragmentInteractionListener mListener;

    protected ListPresenter mPresenter;

    private TextInputLayout mSearchTextInputLayout;
    private ListAdapter mAdapter;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String listFor = getArguments().getString(ARG_LIST_FOR);
            if (Presenter.UNIT.equals(listFor)) {
                mPresenter = UnitsList.sharedInstance();
            } else if (Presenter.MEDICINE.equals(listFor)) {
                mPresenter = MedicinesList.sharedInstance();
            } else if (Presenter.DOCTOR.equals(listFor)) {
                mPresenter = DoctorsList.sharedInstance();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPresenter.openDatabase();
        mPresenter.setView(this);
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mSearchTextInputLayout = (TextInputLayout) view.findViewById(R.id.search_textinputlayout);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(inflater.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        mAdapter = getListAdapter(inflater.getContext());
        recyclerView.setAdapter(mAdapter);
        EditText searchEditText = mSearchTextInputLayout.getEditText();
        assert searchEditText != null;
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                load(s.toString());
                if (null != mAdapter) {
                    mAdapter.setFtsQueries(FtsUtil.getAllPossibleSearchStrings(s.toString()));
                }
            }
        });
        if (null != savedInstanceState) {
            searchEditText.setText(savedInstanceState.getString(KEY_SEARCH_STRING, ""));
        } else {
            load(searchEditText.getText());
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (null == outState) {
            outState = new Bundle();
        }
        if (null != mSearchTextInputLayout) {
            EditText searchEditText = mSearchTextInputLayout.getEditText();
            assert searchEditText != null;
            outState.putString(KEY_SEARCH_STRING, searchEditText.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    private void load(CharSequence text) {
        if (null != mPresenter) {
            mPresenter.load(text);
        }
    }

    protected abstract ListAdapter getListAdapter(Context context);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.setView(null);
        mPresenter.closeDatabase();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
        mListener = null;
    }

    @Override
    public void refresh(Context applicationContext) {
        // When shown in ViewPager, this method may get called even before fragment is created. In
        // this case, protect with null check from NPE
        if (null != mPresenter && null != mSearchTextInputLayout) {
            EditText searchEditText = mSearchTextInputLayout.getEditText();
            assert searchEditText != null;
            load(searchEditText.getText());
        }
    }

    @Override
    public void onFabClicked(View v) {
        if (null != mPresenter) {
            mPresenter.addNew(v.getContext());
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
