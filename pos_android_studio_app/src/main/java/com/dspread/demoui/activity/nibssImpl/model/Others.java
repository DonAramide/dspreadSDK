package com.dspread.demoui.activity.nibssImpl.model;

public class Others {
    private String notification;
    private String notificationhost;
    private Boolean autosend;
    private Boolean smartlinkinuse;
    private Boolean smartnotification;
    private Boolean smartlogon;

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getNotificationhost() {
        return notificationhost;
    }

    public void setNotificationhost(String notificationhost) {
        this.notificationhost = notificationhost;
    }

    public Boolean getAutosend() {
        return autosend;
    }

    public void setAutosend(Boolean autosend) {
        this.autosend = autosend;
    }

    public Boolean getSmartlinkinuse() {
        return smartlinkinuse;
    }

    public void setSmartlinkinuse(Boolean smartlinkinuse) {
        this.smartlinkinuse = smartlinkinuse;
    }

    public Boolean getSmartnotification() {
        return smartnotification;
    }

    public void setSmartnotification(Boolean smartnotification) {
        this.smartnotification = smartnotification;
    }

    public Boolean getSmartlogon() {
        return smartlogon;
    }

    public void setSmartlogon(Boolean smartlogon) {
        this.smartlogon = smartlogon;
    }

    @Override
    public String toString() {
        return "Others{" +
                "notification='" + notification + '\'' +
                ", notificationhost='" + notificationhost + '\'' +
                ", autosend=" + autosend +
                ", smartlinkinuse=" + smartlinkinuse +
                ", smartnotification=" + smartnotification +
                ", smartlogon=" + smartlogon +
                '}';
    }
}
