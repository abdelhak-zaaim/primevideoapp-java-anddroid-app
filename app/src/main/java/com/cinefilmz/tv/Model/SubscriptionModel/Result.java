package com.cinefilmz.tv.Model.SubscriptionModel;

import androidx.annotation.Keep;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class Result {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("type_id")
    @Expose
    private String typeId;
    @SerializedName("android_product_package")
    @Expose
    private String androidProductPackage;
    @SerializedName("ios_product_package")
    @Expose
    private String iosProductPackage;
    @SerializedName("data")
    @Expose
    private List<Datum> data;
    @SerializedName("is_buy")
    @Expose
    private Integer isBuy;

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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getAndroidProductPackage() {
        return androidProductPackage;
    }

    public void setAndroidProductPackage(String androidProductPackage) {
        this.androidProductPackage = androidProductPackage;
    }

    public String getIosProductPackage() {
        return iosProductPackage;
    }

    public void setIosProductPackage(String iosProductPackage) {
        this.iosProductPackage = iosProductPackage;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public Integer getIsBuy() {
        return isBuy;
    }

    public void setIsBuy(Integer isBuy) {
        this.isBuy = isBuy;
    }

}