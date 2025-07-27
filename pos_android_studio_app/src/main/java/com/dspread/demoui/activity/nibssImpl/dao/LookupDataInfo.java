package com.dspread.demoui.activity.nibssImpl.dao;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "rm_lookup_data",primaryKeys = {"lookupCode", "name"}
)
public class LookupDataInfo {

    @NonNull
    private String lookupCode;
    @NonNull
    private String name;
    private String code;
    private String description;
    private String status;
    private String parentId;

    public String getLookupCode() {
        return lookupCode;
    }

    public void setLookupCode(String lookupCode) {
        this.lookupCode = lookupCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
