package com.dspread.demoui.activity.nibssImpl.model;

public class TerminalParams {
    private Data data;
    private String transType;
    private String amount;
    private String colour;
    private String tid;
    private String rrn;
    private String param;
    private String stan;
    private String print;

    public Data getData() {
        return data;
    }

    public String getTransType() {
        return transType;
    }

    public String getAmount() {
        return amount;
    }

    public String getColour() {
        return colour;
    }

    public String getTid() {
        return tid;
    }

    public String getRrn() {
        return rrn;
    }

    public String getParam() {
        return param;
    }

    public String getStan() {
        return stan;
    }

    public String getPrint() {
        return print;
    }

    @Override
    public String toString() {
        return "TerminalParams{" +
                "data=" + data +
                ", transType='" + transType + '\'' +
                ", amount='" + amount + '\'' +
                ", colour='" + colour + '\'' +
                ", tid='" + tid + '\'' +
                ", rrn='" + rrn + '\'' +
                ", param='" + param + '\'' +
                ", stan='" + stan + '\'' +
                ", print='" + print + '\'' +
                '}';
    }
}
