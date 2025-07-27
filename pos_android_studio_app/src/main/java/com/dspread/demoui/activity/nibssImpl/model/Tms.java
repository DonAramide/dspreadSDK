package com.dspread.demoui.activity.nibssImpl.model;

public class Tms {
    private String ip;
    private String port;
    private Boolean dhcp;
    private String localip;
    private String maskip;
    private String getway;
    private String primarydns;
    private String secdns;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public Boolean getDhcp() {
        return dhcp;
    }

    public void setDhcp(Boolean dhcp) {
        this.dhcp = dhcp;
    }

    public String getLocalip() {
        return localip;
    }

    public void setLocalip(String localip) {
        this.localip = localip;
    }

    public String getMaskip() {
        return maskip;
    }

    public void setMaskip(String maskip) {
        this.maskip = maskip;
    }

    public String getGetway() {
        return getway;
    }

    public void setGetway(String getway) {
        this.getway = getway;
    }

    public String getPrimarydns() {
        return primarydns;
    }

    public void setPrimarydns(String primarydns) {
        this.primarydns = primarydns;
    }

    public String getSecdns() {
        return secdns;
    }

    public void setSecdns(String secdns) {
        this.secdns = secdns;
    }
}
