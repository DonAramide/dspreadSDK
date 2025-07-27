package com.dspread.demoui.activity.nibssImpl.model;


import java.util.List;

public class Data {


    private String responseCode;
    private String responseMessage;
    private String description;
    private String channel;


    private String code;
    private Params params;
    private Others others;
    private String ptsp;
    private String contact;
    private String footer;
    private String printlogo;
    private String keepalivetimer;

    private List<DataBeanx> dataBeanx = null;
    private String dowmloadObj;
    private String name;
    private String tel;

    public List<DataBeanx> getDataBeanx() {
        return dataBeanx;
    }

    public void setDataBeanx(List<DataBeanx> dataBeanx) {
        this.dataBeanx = dataBeanx;
    }

    public String getDowmloadObj() {
        return dowmloadObj;
    }

    public void setDowmloadObj(String dowmloadObj) {
        this.dowmloadObj = dowmloadObj;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getCode() {
        return code;
    }

    public Params getData() {
        return params;
    }

    public Others getOthers() {
        return others;
    }

    public String getPtsp() {
        return ptsp;
    }

    public String getContact() {
        return contact;
    }

    public String getFooter() {
        return footer;
    }

    public String getPrintlogo() {
        return printlogo;
    }

    public String getKeepalivetimer() {
        return keepalivetimer;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setData(Params params) {
        this.params = params;
    }

    public void setOthers(Others others) {
        this.others = others;
    }

    public void setPtsp(String ptsp) {
        this.ptsp = ptsp;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public void setPrintlogo(String printlogo) {
        this.printlogo = printlogo;
    }

    public void setKeepalivetimer(String keepalivetimer) {
        this.keepalivetimer = keepalivetimer;
    }


    public Params getParams() {
        return params;
    }


    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setParams(Params params) {
        this.params = params;
    }
}
