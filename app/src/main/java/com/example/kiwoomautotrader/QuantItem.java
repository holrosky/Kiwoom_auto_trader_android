package com.example.kiwoomautotrader;

public class QuantItem {
    private String profitFrom;
    private String profitTo;
    private String Quant;

    private int type;

    public QuantItem(int type) {
        this.profitFrom = "";
        this.profitTo = "";
        this.Quant = "";
        this.type = type;

    }

    public String getProfitFrom() {
        return profitFrom;
    }

    public void setProfitFrom(String profitFrom) {
        this.profitFrom = profitFrom;
    }

    public String getProfitTo() {
        return profitTo;
    }

    public void setProfitTo(String profitTo) {
        this.profitTo = profitTo;
    }

    public String getQuant() {
        return Quant;
    }

    public void setQuant(String quant) {
        Quant = quant;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}