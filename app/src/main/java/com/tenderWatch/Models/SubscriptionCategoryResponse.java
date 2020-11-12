package com.tenderWatch.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SubscriptionCategoryResponse {

    @SerializedName("isDeleted")
    @Expose
    private Boolean isDeleted;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("vat")
    @Expose
    private Integer vat;
    @SerializedName("prices")
    @Expose
    private Prices prices;

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVat() {
        return vat;
    }

    public void setVat(Integer vat) {
        this.vat = vat;
    }

    public Prices getPrices() {
        return prices;
    }

    public void setPrices(Prices prices) {
        this.prices = prices;
    }

    public class Prices {

        @SerializedName("monthly")
        @Expose
        private Integer monthly;
        @SerializedName("yearly")
        @Expose
        private Integer yearly;

        public Integer getMonthly() {
            return monthly;
        }

        public void setMonthly(Integer monthly) {
            this.monthly = monthly;
        }

        public Integer getYearly() {
            return yearly;
        }

        public void setYearly(Integer yearly) {
            this.yearly = yearly;
        }

    }


}
