package com.example.kiwoomautotrader;

import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class IndicatorItem {
    private String name;
    private LinearLayout linearLayout;
    private int type;

    public IndicatorItem(String name, int type) {
        this.name = name;
        this.type = type;

    }

    public String getName() {
        return name;
    }

    public void setName(String text) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public LinearLayout getLinearLayout() {return linearLayout;}

    public void setLinearLayout(LinearLayout linearLayout) {this.linearLayout = linearLayout;}

}