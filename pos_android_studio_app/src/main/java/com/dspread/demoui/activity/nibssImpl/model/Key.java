package com.dspread.demoui.activity.nibssImpl.model;

public class Key {
    private String algorithm;
    private String server;
    private String ctmk;
    private String ctmkPosvas;
    private String ctmkRexConnect;
    private String interswitchserver;
    private String isRecvBankCode;
    private String interswitchdestaccount;





    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getCtmk() {
        return ctmk;
    }

    public void setCtmk(String ctmk) {
        this.ctmk = ctmk;
    }

    public String getInterswitchserver() {
        return interswitchserver;
    }

    public void setInterswitchserver(String interswitchserver) {
        this.interswitchserver = interswitchserver;
    }

    public String getIsRecvBankCode() {
        return isRecvBankCode;
    }

    public void setIsRecvBankCode(String isRecvBankCode) {
        this.isRecvBankCode = isRecvBankCode;
    }

    public String getInterswitchdestaccount() {
        return interswitchdestaccount;
    }

    public void setInterswitchdestaccount(String interswitchdestaccount) {
        this.interswitchdestaccount = interswitchdestaccount;
    }

    public String getCtmkPosvas() {
        return ctmkPosvas;
    }

    public void setCtmkPosvas(String ctmkPosvas) {
        this.ctmkPosvas = ctmkPosvas;
    }

    public String getCtmkRexConnect() {
        return ctmkRexConnect;
    }

    public void setCtmkRexConnect(String ctmkRexConnect) {
        this.ctmkRexConnect = ctmkRexConnect;
    }

    @Override
    public String toString() {
        return "Key{" +
                "algorithm='" + algorithm + '\'' +
                ", server='" + server + '\'' +
                ", ctmk='" + ctmk + '\'' +
                ", ctmkPosvas='" + ctmkPosvas + '\'' +
                ", ctmkRexConnect='" + ctmkRexConnect + '\'' +
                ", interswitchserver='" + interswitchserver + '\'' +
                ", isRecvBankCode='" + isRecvBankCode + '\'' +
                ", interswitchdestaccount='" + interswitchdestaccount + '\'' +
                '}';
    }
}
