package com.aviras.mrassistant.ui.editors;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.aviras.mrassistant.R;

/**
 * Represent single line or multiline text field. It can accept text inputs using EditText
 * <p/>
 * Created by ashish on 8/6/16.
 */
public class TextFieldEditor extends Editor {

    private int lineCount = 1;
    @Nullable
    private CharSequence value;
    private int inputType = EditorInfo.TYPE_CLASS_TEXT;
    private int imeOption = EditorInfo.IME_ACTION_DONE;
    @NonNull
    private TextUtils.TruncateAt ellipsize = TextUtils.TruncateAt.END;

    public TextFieldEditor(int id, int type, @Nullable CharSequence name) {
        super(id, type, name);
    }

    public int getLineCount() {
        return lineCount;
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }

    public
    @Nullable
    CharSequence getValue() {
        return value;
    }

    public void setValue(@Nullable CharSequence value) {
        this.value = value;
    }

    public int getInputType() {
        return inputType;
    }

    public void setInputType(int inputType) {
        this.inputType = inputType;
    }

    public int getImeOption() {
        return imeOption;
    }

    public void setImeOption(int imeOption) {
        this.imeOption = imeOption;
    }

    @NonNull
    public TextUtils.TruncateAt getEllipsize() {
        return ellipsize;
    }

    public void setEllipsize(@NonNull TextUtils.TruncateAt ellipsize) {
        this.ellipsize = ellipsize;
    }

    public static class TextFieldEditorViewHolder extends EditorAdapter.EditorViewHolder
            implements TextWatcher {

        private static final String LOG_TAG = "TxtFildEditorViewHolder";
        TextInputLayout textInputLayout;
        TextFieldEditor tEditor;
        int editTextMinHeight;

        public TextFieldEditorViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_text_field_editor, parent, false));
            textInputLayout = (TextInputLayout) itemView.findViewById(R.id.text_field_textinputlayout);
            editTextMinHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42, parent.getResources().getDisplayMetrics());
        }

        @Override
        void updateView(Editor editor) {
            if (editor instanceof TextFieldEditor) {
                tEditor = (TextFieldEditor) editor;
                textInputLayout.setHint(editor.getName());
                textInputLayout.setEnabled(editor.isEnabled());
                EditText editText = textInputLayout.getEditText();
                if (null != editText) {
                    editText.removeTextChangedListener(this);
                    editText.setInputType(tEditor.getInputType());
                    editText.setImeOptions(tEditor.getImeOption());
                    editText.setText(tEditor.getValue());
//                    editText.getLayoutParams().height = editTextMinHeight * Math.min(tEditor.getLineCount(), 3);
                    editText.setMaxLines(tEditor.getLineCount());
                    editText.setScrollContainer(tEditor.getLineCount() > 1);
                    editText.setVerticalScrollBarEnabled(tEditor.getLineCount() > 1);
                    editText.setScrollbarFadingEnabled(tEditor.getLineCount() <= 1);
                    editText.setEllipsize(tEditor.getEllipsize());
                    editText.addTextChangedListener(this);
                }
                if (tEditor.getValidator() != null && !tEditor.getValidator().validate()) {
                    textInputLayout.setErrorEnabled(true);
                    textInputLayout.setError(tEditor.getErrorMessasge());
                } else {
                    textInputLayout.setErrorEnabled(false);
                    textInputLayout.setError(null);
                }
            } else {
                Log.w(LOG_TAG, "Wrong editor passed to this view");
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
// Following code can be used to prevent user from entering more lines than specified
//            EditText editText = textInputLayout.getEditText();
//            if (null != editText) {
//                if (null != editText.getLayout() && editText.getLayout().getLineCount() > tEditor.getLineCount()) {
//                    editText.getText().delete(editText.getText().length() - 1, editText.getText().length());
//                }
//            }
            tEditor.setValue(s.toString());
            if (tEditor.getValidator() != null && !tEditor.getValidator().validate()) {
                textInputLayout.setErrorEnabled(true);
                textInputLayout.setError(tEditor.getErrorMessasge());
            } else {
                textInputLayout.setErrorEnabled(false);
                textInputLayout.setError(null);
            }
        }
    }
}
