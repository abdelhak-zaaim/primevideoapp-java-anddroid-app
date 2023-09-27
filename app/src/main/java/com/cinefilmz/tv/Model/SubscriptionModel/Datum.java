package com.cinefilmz.tv.Model.SubscriptionModel;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class Datum {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("package_id")
    @Expose
    private Integer packageId;
    @SerializedName("package_key")
    @Expose
    private String packageKey;
    @SerializedName("package_value")
    @Expose
    private String packageValue;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPackageId() {
        return packageId;
    }

    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }

    public String getPackageKey() {
        return packageKey;
    }

    public void setPackageKey(String packageKey) {
        this.packageKey = packageKey;
    }

    public String getPackageValue() {
        return packageValue;
    }

    public void setPackageValue(String packageValue) {
        this.packageValue = packageValue;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}