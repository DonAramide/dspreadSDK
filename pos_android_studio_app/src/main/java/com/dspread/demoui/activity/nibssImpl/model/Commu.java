package com.dspread.demoui.activity.nibssImpl.model;

public class Commu {
    private String type;
    private Boolean ssl;
    private Boolean posvasssl;
    private String timeout;
    private String hostname;
    private Mobile mobile;
    private Ethernet ethernet;
    private Tms tms;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getSsl() {
        return ssl;
    }

    public void setSsl(Boolean ssl) {
        this.ssl = ssl;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Mobile getMobile() {
        return mobile;
    }

    public void setMobile(Mobile mobile) {
        this.mobile = mobile;
    }

    public Ethernet getEthernet() {
        return ethernet;
    }

    public void setEthernet(Ethernet ethernet) {
        this.ethernet = ethernet;
    }

    public Tms getTms() {
        return tms;
    }

    public void setTms(Tms tms) {
        this.tms = tms;
    }

    public Boolean getPosvasssl() {
        return posvasssl;
    }

    public void setPosvasssl(Boolean posvasssl) {
        this.posvasssl = posvasssl;
    }

    @Override
    public String toString() {
        return "Commu{" +
                "type='" + type + '\'' +
                ", ssl=" + ssl +
                ", posvasssl=" + posvasssl +
                ", timeout='" + timeout + '\'' +
                ", hostname='" + hostname + '\'' +
                ", mobile=" + mobile +
                ", ethernet=" + ethernet +
                ", tms=" + tms +
                '}';
    }
}
