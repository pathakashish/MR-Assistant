package com.aviras.mrassistant.ui.editors;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.aviras.mrassistant.R;

import java.util.ArrayList;
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

    private static final String ARG_EDITING_FOR = "editing_for";
    public static final String TAG = "EditorFragment";

    private String mEditingFor;

    private OnFragmentInteractionListener mListener;

    public static EditorFragment newInstance(String editingFor) {
        EditorFragment fragment = new EditorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EDITING_FOR, editingFor);
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
            mEditingFor = getArguments().getString(ARG_EDITING_FOR);
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

        List<Editor> editors = new ArrayList<>();

        TextFieldEditor name = EditorFactory.newTextFieldEditor(inflater.getContext(), R.string.doctors_name_here, "", 1);
        name.setRequired(true);
        name.setValidator(new Editor.Validator<TextFieldEditor>(name) {

            @Override
            public boolean validate() {
                TextFieldEditor text = getField();
                return text.isRequired() && !TextUtils.isEmpty(text.getValue());
            }
        }, inflater.getContext().getString(R.string.error_name_required));
        name.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME);
        name.setImeOption(EditorInfo.IME_ACTION_NEXT);
        editors.add(name);

        TextFieldEditor contactNumber = EditorFactory.newTextFieldEditor(inflater.getContext(), R.string.mob_phone_etc, "", 1);
        contactNumber.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        contactNumber.setImeOption(EditorInfo.IME_ACTION_NEXT);
        editors.add(contactNumber);

        TextFieldEditor address = EditorFactory.newTextFieldEditor(inflater.getContext(), R.string.delivery_address, "", 5);
        address.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_POSTAL_ADDRESS | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
        address.setImeOption(EditorInfo.IME_ACTION_NONE);
        editors.add(address);

        TextFieldEditor note = EditorFactory.newTextFieldEditor(inflater.getContext(), R.string.any_other_info, "", 3);
        note.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_LONG_MESSAGE | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
        note.setImeOption(EditorInfo.IME_ACTION_NONE);
        editors.add(note);

        adapter.setEditors(editors);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
