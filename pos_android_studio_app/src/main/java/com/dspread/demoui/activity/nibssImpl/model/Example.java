package com.dspread.demoui.activity.nibssImpl.model;

public class Example {


    private Integer code;
    private Data data;
    private String msssage;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMsssage() {
        return msssage;
    }

    public void setMsssage(String msssage) {
        this.msssage = msssage;
    }
    @Override
    public String toString() {
        return "Example{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }
}
