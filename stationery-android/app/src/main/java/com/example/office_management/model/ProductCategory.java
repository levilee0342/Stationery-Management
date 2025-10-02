package com.example.office_management.model;

import java.util.List;

public class ProductCategory {
    private String name;
    private List<String> subCategories;
    private int imageResId;

    public ProductCategory(String name, List<String> subCategories, int imageResId){
        this.name = name;
        this.subCategories = subCategories;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public List<String> getSubCategories() {
        return subCategories;
    }

    public int getImageResId() {
        return imageResId;
    }
}
