package com.dspread.demoui.activity.nibssImpl.model;

public class Terminal {
    private String acctype;
    private String acquirer;
    private String bank;
    private String currname;
    private String currcode;

    public String getAcctype() {
        return acctype;
    }

    public void setAcctype(String acctype) {
        this.acctype = acctype;
    }

    public String getAcquirer() {
        return acquirer;
    }

    public void setAcquirer(String acquirer) {
        this.acquirer = acquirer;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getCurrname() {
        return currname;
    }

    public void setCurrname(String currname) {
        this.currname = currname;
    }

    public String getCurrcode() {
        return currcode;
    }

    public void setCurrcode(String currcode) {
        this.currcode = currcode;
    }

    @Override
    public String toString() {
        return "Terminal{" +
                "acctype='" + acctype + '\'' +
                ", acquirer='" + acquirer + '\'' +
                ", bank='" + bank + '\'' +
                ", currname='" + currname + '\'' +
                ", currcode='" + currcode + '\'' +
                '}';
    }
}
