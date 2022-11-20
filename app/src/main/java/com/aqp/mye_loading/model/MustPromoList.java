package com.aqp.mye_loading.model;

public class MustPromoList {
    public String promocode;
    public int price;
    public String descriptions;
    public int id;

    public MustPromoList(int id, String promocode, int price, String descriptions) {
        this.id = id;
        this.promocode = promocode;
        this.price = price;
        this.descriptions = descriptions;
    }

    public MustPromoList() {

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

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
