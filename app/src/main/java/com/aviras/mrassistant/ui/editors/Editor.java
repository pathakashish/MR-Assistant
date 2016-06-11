package com.aviras.mrassistant.ui.editors;

import android.support.annotation.Nullable;

/**
 * Base class for all editor views. This will be used by editor activity to show forms to user.
 * <p/>
 * Created by ashish on 8/6/16.
 */
public abstract class Editor {

    private int id;
    private int type;
    @Nullable
    private CharSequence name;
    private boolean required;
    @Nullable
    private CharSequence errorMessasge;
    private boolean enabled;
    private Validator<? extends Editor> validator;

    public Editor(int id, int type, @Nullable CharSequence name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public
    @Nullable
    CharSequence getName() {
        return name;
    }

    public void setName(@Nullable CharSequence name) {
        this.name = name;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public
    @Nullable
    CharSequence getErrorMessasge() {
        return errorMessasge;
    }

    public void setErrorMessasge(@Nullable CharSequence errorMessasge) {
        this.errorMessasge = errorMessasge;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator, CharSequence errorString) {
        this.validator = validator;
        setErrorMessasge(errorString);
    }

    public static abstract class Validator<T> {
        private T field;

        public Validator(T field) {
            this.field = field;
        }

        public T getField() {
            return field;
        }

        public void setField(T field) {
            this.field = field;
        }

        public abstract boolean validate();
    }
}
