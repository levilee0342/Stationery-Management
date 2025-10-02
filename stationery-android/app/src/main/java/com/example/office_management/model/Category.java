package com.example.office_management.model;

public class Category {
    private String categoryId;
    private String categoryName;
    private String icon;
    private String bgColor;

    public Category(String categoryId, String categoryName, String icon, String bgColor) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.icon = icon;
        this.bgColor = bgColor;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }
}
