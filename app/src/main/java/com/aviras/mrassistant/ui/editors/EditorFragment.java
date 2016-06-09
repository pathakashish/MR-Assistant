package com.aviras.mrassistant.ui.editors;

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

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditorFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditorFragment extends Fragment {

    public static final String TAG = "EditorFragment";

    private String mEditingFor;

    private OnFragmentInteractionListener mListener;
    private EditorPresenter mPresenter;

    public static EditorFragment newInstance(String editingFor) {
        EditorFragment fragment = new EditorFragment();
        Bundle args = new Bundle();
        args.putString(EditorActivity.EXTRA_EDITING_FOR, editingFor);
        fragment.setArguments(args);
        return fragment;
    }

    public EditorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEditingFor = getArguments().getString(EditorActivity.EXTRA_EDITING_FOR);
            if (EditorActivity.UNIT.equals(mEditingFor)) {
                mPresenter = UnitPresenter.sharedInstance();
            } else if (EditorActivity.MEDICINE.equals(mEditingFor)) {
                mPresenter = MedicinePresenter.sharedInstance();
            } else if (EditorActivity.DOCTOR.equals(mEditingFor)) {
                mPresenter = DoctorPresenter.sharedInstance();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editor, container, false);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext(), LinearLayoutManager.VERTICAL, false));
        EditorAdapter adapter = new EditorAdapter();
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        List<Editor> editors = mPresenter.getEditors(inflater.getContext());

        adapter.setEditors(editors);
        return view;
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
