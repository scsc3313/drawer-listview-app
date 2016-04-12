package com.example.hsh.healthrecordapp.model;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ViewHolder {

    private ViewHolder viewHolder;

    public EditText nameEdit, weightEdit;
    public TextView setEdit, countEdit;
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

    public TextView getSetEdit() {
        return setEdit;
    }

    public TextView getCountEdit() {
        return countEdit;
    }

    public Button getButton() {
        return button;
    }
}