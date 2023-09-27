package com.cinefilmz.tv.Model.PayTmModel;

import android.util.Log;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class Paytm {

    @SerializedName("MID")
    String mId;

    @SerializedName("ORDER_ID")
    String orderId;

    @SerializedName("CUST_ID")
    String custId;

    @SerializedName("CHANNEL_ID")
    String channelId;

    @SerializedName("TXN_AMOUNT")
    String txnAmount;

    @SerializedName("WEBSITE")
    String website;

    @SerializedName("CALLBACK_URL")
    String callBackUrl;

    @SerializedName("INDUSTRY_TYPE_ID")
    String industryTypeId;

    public Paytm(String mId, String orderID, String custID, String channelId, String txnAmount, String website, String callBackUrl, String industryTypeId) {
        this.mId = mId;
        this.orderId = orderID;
        this.custId = custID;
        this.channelId = channelId;
        this.txnAmount = txnAmount;
        this.website = website;
        this.callBackUrl = callBackUrl;
        this.industryTypeId = industryTypeId;

        Log.e("orderId", orderId);
        Log.e("customerId", custId);
    }

    public String getmId() {
        return mId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustId() {
        return custId;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getTxnAmount() {
        return txnAmount;
    }

    public String getWebsite() {
        return website;
    }

    public String getCallBackUrl() {
        return callBackUrl;
    }

    public String getIndustryTypeId() {
        return industryTypeId;
    }

}