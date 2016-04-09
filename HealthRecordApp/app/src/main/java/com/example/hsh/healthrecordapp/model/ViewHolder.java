package com.example.hsh.healthrecordapp.model;

import android.widget.Button;
import android.widget.EditText;

public class ViewHolder {

    private ViewHolder viewHolder;

    public EditText nameEdit, weightEdit, setEdit, countEdit;
    public Button button;
    public int id;

    public int getId() {
        return id;
    }

    public EditText getNameEdit() {
        return nameEdit;
    }

    public EditText getWeightEdit() {
        return weightEdit;
    }

    public EditText getSetEdit() {
        return setEdit;
    }

    public EditText getCountEdit() {
        return countEdit;
    }

    public Button getButton() {
        return button;
    }
}