package com.engineerskasa.safetapp.Model;

import androidx.annotation.NonNull;

public class CaterygoryObject {
    private String categoryTitle;

    public CaterygoryObject() {
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
