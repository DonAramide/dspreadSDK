package com.dspread.demoui.activity.nibssImpl.model;

import java.util.List;

public class DataBeanx {
    private List<String> entryTypes = null;
    private String mid;
    private List<String> paymentMethods = null;
    private List<String> paymentTypes = null;
    private String tid;

    public List<String> getEntryTypes() {
        return entryTypes;
    }

    public void setEntryTypes(List<String> entryTypes) {
        this.entryTypes = entryTypes;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public List<String> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<String> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public List<String> getPaymentTypes() {
        return paymentTypes;
    }

    public void setPaymentTypes(List<String> paymentTypes) {
        this.paymentTypes = paymentTypes;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }
}
