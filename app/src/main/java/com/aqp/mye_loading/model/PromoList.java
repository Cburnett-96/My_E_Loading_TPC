package com.aqp.mye_loading.model;

public class PromoList {
    public String promocode;
    public int price;
    public String sms;
    public String call;
    public String data;
    public String validity;
    public int id;

    public PromoList(int id, String promocode, int price, String sms, String call, String data, String validity) {
        this.id = id;
        this.promocode = promocode;
        this.price = price;
        this.sms = sms;
        this.call = call;
        this.data = data;
        this.validity = validity;
    }

    public PromoList() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPromocode() {
        return promocode;
    }

    public void setPromocode(String promocode) {
        this.promocode = promocode;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getCall() {
        return call;
    }

    public void setCall(String call) {
        this.call = call;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }
}
