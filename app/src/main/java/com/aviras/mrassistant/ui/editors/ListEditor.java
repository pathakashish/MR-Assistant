package com.aviras.mrassistant.ui.editors;

import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aviras.mrassistant.R;

/**
 * Represent list in the editor.
 * <p/>
 * Created by ashish on 8/6/16.
 */
public class ListEditor extends Editor {

    private boolean isNestedInScrollableView = false;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public ListEditor(int id, int type, @Nullable CharSequence name, RecyclerView.Adapter adapter, RecyclerView.LayoutManager layoutManager, boolean isNestedInScrollableView) {
        super(id, type, name);
        this.isNestedInScrollableView = isNestedInScrollableView;
        this.adapter = adapter;
        this.layoutManager = layoutManager;
    }

    public boolean isNestedInScrollableView() {
        return isNestedInScrollableView;
    }

    public void setNestedInScrollableView(boolean nestedInScrollableView) {
        isNestedInScrollableView = nestedInScrollableView;
    }

    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return layoutManager;
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    public static class ListEditorViewHolder extends EditorAdapter.EditorViewHolder {

        private static final String LOG_TAG = "SlctbleLstEdtrVewHldr";
        RecyclerView recyclerView;
        AppCompatTextView errorTextView;
        ListEditor lEditor;

        public ListEditorViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_editor, parent, false));
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerview);
            errorTextView = (AppCompatTextView) itemView.findViewById(R.id.error_message_textview);
        }

        @Override
        void updateView(Editor editor) {
            if (editor instanceof ListEditor) {
                lEditor = (ListEditor) editor;
                recyclerView.setNestedScrollingEnabled(!lEditor.isNestedInScrollableView());
                recyclerView.setLayoutManager(lEditor.getLayoutManager());
                recyclerView.setAdapter(lEditor.getAdapter());

                if (lEditor.getValidator() != null && !lEditor.getValidator().validate()) {
                    errorTextView.setText(lEditor.getErrorMessasge());
                    errorTextView.setVisibility(View.VISIBLE);
                } else {
                    errorTextView.setVisibility(View.GONE);
                }
            } else {
                Log.w(LOG_TAG, "Wrong editor passed to this view");
            }
        }
    }

}
