package com.dspread.demoui.activity.nibssImpl.model;

public class Password {
    private String admin;
    private String safe;
    private String opera;
    private String supervisor;

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getSafe() {
        return safe;
    }

    public void setSafe(String safe) {
        this.safe = safe;
    }

    public String getOpera() {
        return opera;
    }

    public void setOpera(String opera) {
        this.opera = opera;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    @Override
    public String toString() {
        return "Password{" +
                "admin='" + admin + '\'' +
                ", safe='" + safe + '\'' +
                ", opera='" + opera + '\'' +
                ", supervisor='" + supervisor + '\'' +
                '}';
    }
}
