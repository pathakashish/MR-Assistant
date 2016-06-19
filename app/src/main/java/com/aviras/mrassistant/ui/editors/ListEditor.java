package com.aviras.mrassistant.ui.editors;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.logger.Log;
import com.aviras.mrassistant.ui.utils.DividerItemDecoration;

/**
 * Represent list in the editor.
 * <p/>
 * Created by ashish on 8/6/16.
 */
public class ListEditor extends Editor {

    private boolean isNestedInScrollableView = false;
    private ListEditorAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public ListEditor(int id, int type, @Nullable CharSequence name, ListEditorAdapter adapter, RecyclerView.LayoutManager layoutManager, boolean isNestedInScrollableView) {
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

    public ListEditorAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(ListEditorAdapter adapter) {
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
        AppCompatTextView nameTextView;
        AppCompatTextView errorTextView;
        ListEditor lEditor;

        public ListEditorViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_editor, parent, false));
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerview);
            errorTextView = (AppCompatTextView) itemView.findViewById(R.id.error_message_textview);
            nameTextView = (AppCompatTextView) itemView.findViewById(R.id.name_textview);
        }

        @Override
        void updateView(Editor editor) {
            if (editor instanceof ListEditor) {
                lEditor = (ListEditor) editor;
                recyclerView.setNestedScrollingEnabled(!lEditor.isNestedInScrollableView());
                recyclerView.setLayoutManager(lEditor.getLayoutManager());
                recyclerView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.divider_drawable_1dp)));
                recyclerView.setAdapter(lEditor.getAdapter());
                if (null != lEditor.getAdapter()) {
                    lEditor.getAdapter().setListEditorViewHolder(this);
                }

                updateErrorView();

                if (TextUtils.isEmpty(lEditor.getName())) {
                    nameTextView.setVisibility(View.GONE);
                } else {
                    nameTextView.setText(lEditor.getName());
                    nameTextView.setVisibility(View.VISIBLE);
                }
            } else {
                Log.w(LOG_TAG, "Wrong editor passed to this view");
            }
        }

        void updateErrorView() {
            if (lEditor.getValidator() != null && !lEditor.getValidator().validate()) {
                errorTextView.setText(lEditor.getErrorMessasge());
                errorTextView.setVisibility(View.VISIBLE);
            } else {
                errorTextView.setVisibility(View.GONE);
            }
        }
    }

    public static abstract class ListEditorAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

        private ListEditorViewHolder listEditorViewHolder;
        private RecyclerView.AdapterDataObserver externalObserver;
        // localObserver will registered as RecyclerView.AdapterDataObserver and will proxy calls to
        // actual RecyclerView.AdapterDataObserver which was passed to registeredDatasetObserver as
        // argument. Purpose is to facilitate real time validate on each change.
        private RecyclerView.AdapterDataObserver localObserver;

        private RecyclerView.AdapterDataObserver getLocalObserver() {
            localObserver = new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    notifyListEditorViewHolder();
                    if (null != externalObserver)
                        externalObserver.onChanged();
                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount) {
                    notifyListEditorViewHolder();
                    if (null != externalObserver)
                        externalObserver.onItemRangeChanged(positionStart, itemCount);
                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                    notifyListEditorViewHolder();
                    if (null != externalObserver)
                        externalObserver.onItemRangeChanged(positionStart, itemCount, payload);
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    notifyListEditorViewHolder();
                    if (null != externalObserver)
                        externalObserver.onItemRangeInserted(positionStart, itemCount);
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    notifyListEditorViewHolder();
                    if (null != externalObserver)
                        externalObserver.onItemRangeRemoved(positionStart, itemCount);
                }

                @Override
                public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                    notifyListEditorViewHolder();
                    if (null != externalObserver)
                        externalObserver.onItemRangeMoved(fromPosition, toPosition, itemCount);
                }
            };
            return localObserver;
        }

        private void notifyListEditorViewHolder() {
            if (null != listEditorViewHolder) {
                listEditorViewHolder.updateErrorView();
            }
        }

        @Override
        public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
            super.registerAdapterDataObserver(getLocalObserver());
            externalObserver = observer;
        }

        @Override
        public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
            super.unregisterAdapterDataObserver(localObserver);
            externalObserver = null;
            localObserver = null;
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
            listEditorViewHolder = null;
        }

        public void setListEditorViewHolder(ListEditorViewHolder listEditorViewHolder) {
            this.listEditorViewHolder = listEditorViewHolder;
        }
    }
}
