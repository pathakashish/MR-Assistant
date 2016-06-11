package com.aviras.mrassistant.ui.editors;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Shows {@link Editor} list. This adapter can be used to show editable forms.
 * To support new type of editable field in this form, one must extend the {@link Editor} to provide
 * model representation of the field and extend the {@link EditorViewHolder} to providegraphical
 * reoresentation of the field.
 * Refer {@link TextFieldEditor}, {@link com.aviras.mrassistant.ui.editors.TextFieldEditor.TextFieldEditorViewHolder}
 * and {@link SelectableListEditor}, {@link com.aviras.mrassistant.ui.editors.SelectableListEditor.SelectableListEditorViewHolder}
 * for sample implementation.
 * <p/>
 * Created by ashish on 9/6/16.
 */
public class EditorAdapter extends RecyclerView.Adapter<EditorAdapter.EditorViewHolder> {

    private List<Editor> mEditors;

    public List<Editor> getEditors() {
        return mEditors;
    }

    public void setEditors(List<Editor> editors) {
        mEditors = editors;
    }

    @Override
    public EditorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        EditorViewHolder holder;
        if (EditorFactory.VIEW_TYPE_TEXT_FIELD_EDITOR == viewType) {
            holder = new TextFieldEditor.TextFieldEditorViewHolder(parent);
        } else if (EditorFactory.VIEW_TYPE_SELECTABLE_LIST_EDITOR == viewType) {
            holder = new SelectableListEditor.SelectableListEditorViewHolder(parent);
        } else {
            holder = null;
        }
        if (null == holder) {
            throw new IllegalStateException("holder cannot be null. No viewType matches value " + viewType);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(EditorViewHolder holder, int position) {
        holder.updateView(mEditors.get(position));
    }

    @Override
    public int getItemCount() {
        int count = null == mEditors ? 0 : mEditors.size();
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        return mEditors.get(position).getType();
    }

    public static abstract class EditorViewHolder extends RecyclerView.ViewHolder {

        public EditorViewHolder(View itemView) {
            super(itemView);
        }

        abstract void updateView(Editor editor);
    }
}
