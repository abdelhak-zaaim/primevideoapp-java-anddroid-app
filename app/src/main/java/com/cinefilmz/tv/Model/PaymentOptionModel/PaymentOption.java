package com.cinefilmz.tv.Model.PaymentOptionModel;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class PaymentOption {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("visibility")
    @Expose
    private String visibility;
    @SerializedName("is_live")
    @Expose
    private String isLive;
    @SerializedName("live_key_1")
    @Expose
    private String liveKey1;
    @SerializedName("live_key_2")
    @Expose
    private String liveKey2;
    @SerializedName("live_key_3")
    @Expose
    private String liveKey3;
    @SerializedName("test_key_1")
    @Expose
    private String testKey1;
    @SerializedName("test_key_2")
    @Expose
    private String testKey2;
    @SerializedName("test_key_3")
    @Expose
    private String testKey3;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getIsLive() {
        return isLive;
    }

    public void setIsLive(String isLive) {
        this.isLive = isLive;
    }

    public String getLiveKey1() {
        return liveKey1;
    }

    public void setLiveKey1(String liveKey1) {
        this.liveKey1 = liveKey1;
    }

    public String getLiveKey2() {
        return liveKey2;
    }

    public void setLiveKey2(String liveKey2) {
        this.liveKey2 = liveKey2;
    }

    public String getLiveKey3() {
        return liveKey3;
    }

    public void setLiveKey3(String liveKey3) {
        this.liveKey3 = liveKey3;
    }

    public String getTestKey1() {
        return testKey1;
    }

    public void setTestKey1(String testKey1) {
        this.testKey1 = testKey1;
    }

    public String getTestKey2() {
        return testKey2;
    }

    public void setTestKey2(String testKey2) {
        this.testKey2 = testKey2;
    }

    public String getTestKey3() {
        return testKey3;
    }

    public void setTestKey3(String testKey3) {
        this.testKey3 = testKey3;
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