package com.aviras.mrassistant.ui.editors;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.aviras.mrassistant.R;

import java.util.List;

/**
 * Represent selectable list.
 * Created by ashish on 8/6/16.
 */
public class SelectableListEditor extends Editor {

    private boolean allowMultiSelect;
    private List<SelectableListEditorItem> items;

    public SelectableListEditor(int id, int type, @Nullable CharSequence name) {
        super(id, type, name);
    }

    public boolean isAllowMultiSelect() {
        return allowMultiSelect;
    }

    public void setAllowMultiSelect(boolean allowMultiSelect) {
        this.allowMultiSelect = allowMultiSelect;
    }

    public List<SelectableListEditorItem> getItems() {
        return items;
    }

    public void setItems(List<SelectableListEditorItem> items) {
        this.items = items;
    }

    public interface SelectableListEditorItem {
        CharSequence getName();

        boolean isSelected();
    }

    public static class SelectableListEditorViewHolder extends EditorAdapter.EditorViewHolder {

        public SelectableListEditorViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_selectable_list_editor, parent, false));
        }

        @Override
        void updateView(Editor editor) {

        }
    }
}
