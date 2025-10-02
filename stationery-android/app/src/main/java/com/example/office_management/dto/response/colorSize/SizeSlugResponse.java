package com.example.office_management.dto.response.colorSize;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class SizeSlugResponse {
    @SerializedName("size")
    private String size;

    @SerializedName("slug")
    private String slug;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SizeSlugResponse that = (SizeSlugResponse) o;
        return Objects.equals(size, that.size) && Objects.equals(slug, that.slug);
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, slug);
    }

    @Override
    public String toString() {
        return "SizeSlugResponse{" +
                "size='" + size + '\'' +
                ", slug='" + slug + '\'' +
                '}';
    }
}