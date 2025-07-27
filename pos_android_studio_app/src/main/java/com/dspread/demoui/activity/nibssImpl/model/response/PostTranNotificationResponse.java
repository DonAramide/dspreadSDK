package com.dspread.demoui.activity.nibssImpl.model.response;

public class PostTranNotificationResponse {


    private String code;
    private String id;
    private String desc;
    private String refNo;





    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    @Override
    public String toString() {
        return "PostTranNotificationResponse{" +
                "code='" + code + '\'' +
                ", id='" + id + '\'' +
                ", desc='" + desc + '\'' +
                ", refNo='" + refNo + '\'' +
                '}';
    }
}
