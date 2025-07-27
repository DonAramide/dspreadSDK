package com.dspread.demoui.activity.nibssImpl.dao;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "rm_master_category",primaryKeys = {"categoryType", "name"}
)
public class MasterCategoryInfo {

    @NonNull
    private String categoryType;
    @NonNull
    private String name;
    private String logo;
    private String description;
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }



    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
