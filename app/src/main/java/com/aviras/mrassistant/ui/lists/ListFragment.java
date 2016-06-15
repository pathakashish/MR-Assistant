package com.aviras.mrassistant.ui.lists;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.ui.FabActionProvider;
import com.aviras.mrassistant.ui.Presenter;
import com.aviras.mrassistant.ui.Refreshable;
import com.aviras.mrassistant.ui.TitleProvider;
import com.aviras.mrassistant.ui.doctors.DoctorsList;
import com.aviras.mrassistant.ui.medicines.MedicinesList;
import com.aviras.mrassistant.ui.units.UnitsList;

import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Shows list of {@link RealmObject}
 */
public abstract class ListFragment<T extends RealmObject> extends Fragment implements ListView<T>, TitleProvider, Refreshable, FabActionProvider {

    protected static final String ARG_LIST_FOR = "list_for";

    private OnFragmentInteractionListener mListener;

    protected ListPresenter mPresenter;
    private LinearLayoutManager mLayoutManager;

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
        mPresenter.load();
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mLayoutManager = new LinearLayoutManager(inflater.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(getListAdapter(inflater.getContext()));
        return view;
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
        if (null != mPresenter) {
            mPresenter.load();
        }
    }

    @Override
    public void onFabClicked(View v) {
        mPresenter.addNew(v.getContext());
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
