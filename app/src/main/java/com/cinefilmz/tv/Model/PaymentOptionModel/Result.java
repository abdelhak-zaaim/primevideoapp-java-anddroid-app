package com.cinefilmz.tv.Model.PaymentOptionModel;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class Result {

    @SerializedName("inapppurchage")
    @Expose
    private PaymentOption inapppurchage;
    @SerializedName("paypal")
    @Expose
    private PaymentOption paypal;
    @SerializedName("razorpay")
    @Expose
    private PaymentOption razorpay;
    @SerializedName("flutterwave")
    @Expose
    private PaymentOption flutterwave;
    @SerializedName("payumoney")
    @Expose
    private PaymentOption payumoney;
    @SerializedName("paytm")
    @Expose
    private PaymentOption paytm;
    @SerializedName("stripe")
    @Expose
    private PaymentOption stripe;
    @SerializedName("cash")
    @Expose
    private PaymentOption cash;

    public PaymentOption getInapppurchage() {
        return inapppurchage;
    }

    public void setInapppurchage(PaymentOption inapppurchage) {
        this.inapppurchage = inapppurchage;
    }

    public PaymentOption getPaypal() {
        return paypal;
    }

    public void setPaypal(PaymentOption paypal) {
        this.paypal = paypal;
    }

    public PaymentOption getRazorpay() {
        return razorpay;
    }

    public void setRazorpay(PaymentOption razorpay) {
        this.razorpay = razorpay;
    }

    public PaymentOption getFlutterwave() {
        return flutterwave;
    }

    public void setFlutterwave(PaymentOption flutterwave) {
        this.flutterwave = flutterwave;
    }

    public PaymentOption getPayumoney() {
        return payumoney;
    }

    public void setPayumoney(PaymentOption payumoney) {
        this.payumoney = payumoney;
    }

    public PaymentOption getPaytm() {
        return paytm;
    }

    public void setPaytm(PaymentOption paytm) {
        this.paytm = paytm;
    }

    public PaymentOption getStripe() {
        return stripe;
    }

    public void setStripe(PaymentOption stripe) {
        this.stripe = stripe;
    }

    public PaymentOption getCash() {
        return cash;
    }

    public void setCash(PaymentOption cash) {
        this.cash = cash;
    }

}
