package com.example.kiwoomautotrader;

import android.widget.LinearLayout;

public class TimeItem {
    private String startHour;
    private String startMin;
    private String endHour;
    private String endMin;
    private int type;

    public TimeItem(int type) {
        this.startHour = "";
        this.startMin = "";
        this.endHour = "";
        this.endMin = "";
        this.type = type;

    }
    public String getStartHour() {
        return startHour;
    }

    public void setStartHour(String startHour) {
        this.startHour = startHour;
    }

    public String getStartMin() {
        return startMin;
    }

    public void setStartMin(String startMin) {
        this.startMin = startMin;
    }

    public String getEndHour() {
        return endHour;
    }

    public void setEndHour(String endHour) {
        this.endHour = endHour;
    }

    public String getEndMin() {
        return endMin;
    }

    public void setEndMin(String endMin) {
        this.endMin = endMin;
    }

    public int getType() {
        return type;
    }
}