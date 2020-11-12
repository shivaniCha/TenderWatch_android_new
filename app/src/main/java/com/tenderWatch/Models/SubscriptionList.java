package com.tenderWatch.Models;

/**
 * Created by ajeetsingh on 04/09/18.
 */

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubscriptionList implements Parcelable {
    @SerializedName("categoryId")
    @Expose
    private List<CategoryId> categoryId = null;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("countryId")
    @Expose
    private CountryId countryId;
    @SerializedName("subscriptionTime")
    @Expose
    private String subscriptionTime;
    @SerializedName("expiredAt")
    @Expose
    private String expiredAt;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("__v")
    @Expose
    private Integer v;
    @SerializedName("planType")
    @Expose
    private SubscriptionCategoryResponse planType;

    protected SubscriptionList(Parcel in) {
        id = in.readString();
        userId = in.readString();
        subscriptionTime = in.readString();
        expiredAt = in.readString();
        createdAt = in.readString();
        if (in.readByte() == 0) {
            v = null;
        } else {
            v = in.readInt();
        }
    }

    public static final Creator<SubscriptionList> CREATOR = new Creator<SubscriptionList>() {
        @Override
        public SubscriptionList createFromParcel(Parcel in) {
            return new SubscriptionList(in);
        }

        @Override
        public SubscriptionList[] newArray(int size) {
            return new SubscriptionList[size];
        }
    };

    public List<CategoryId> getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(List<CategoryId> categoryId) {
        this.categoryId = categoryId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public CountryId getCountryId() {
        return countryId;
    }

    public void setCountryId(CountryId countryId) {
        this.countryId = countryId;
    }

    public String getSubscriptionTime() {
        return subscriptionTime;
    }

    public void setSubscriptionTime(String subscriptionTime) {
        this.subscriptionTime = subscriptionTime;
    }

    public String getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(String expiredAt) {
        this.expiredAt = expiredAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(userId);
        parcel.writeString(subscriptionTime);
        parcel.writeString(expiredAt);
        parcel.writeString(createdAt);
        if (v == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(v);
        }
    }

    public SubscriptionCategoryResponse getPlanType() {
        return planType;
    }

    public void setPlanType(SubscriptionCategoryResponse planType) {
        this.planType = planType;
    }

    public class CategoryId {

        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("__v")
        @Expose
        private Integer v;
        @SerializedName("categoryName")
        @Expose
        private String categoryName;
        @SerializedName("createdAt")
        @Expose
        private String createdAt;
        @SerializedName("imgString")
        @Expose
        private String imgString;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Integer getV() {
            return v;
        }

        public void setV(Integer v) {
            this.v = v;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getImgString() {
            return imgString;
        }

        public void setImgString(String imgString) {
            this.imgString = imgString;
        }

    }

    public class CountryId {

        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("countryName")
        @Expose
        private String countryName;
        @SerializedName("countryCode")
        @Expose
        private String countryCode;
        @SerializedName("isoCode")
        @Expose
        private String isoCode;
        @SerializedName("isoCurrencyCode")
        @Expose
        private String isoCurrencyCode;
        @SerializedName("createdAt")
        @Expose
        private String createdAt;
        @SerializedName("__v")
        @Expose
        private Integer v;
        @SerializedName("imageString")
        @Expose
        private String imageString;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCountryName() {
            return countryName;
        }

        public void setCountryName(String countryName) {
            this.countryName = countryName;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public String getIsoCode() {
            return isoCode;
        }

        public void setIsoCode(String isoCode) {
            this.isoCode = isoCode;
        }

        public String getIsoCurrencyCode() {
            return isoCurrencyCode;
        }

        public void setIsoCurrencyCode(String isoCurrencyCode) {
            this.isoCurrencyCode = isoCurrencyCode;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public Integer getV() {
            return v;
        }

        public void setV(Integer v) {
            this.v = v;
        }

        public String getImageString() {
            return imageString;
        }

        public void setImageString(String imageString) {
            this.imageString = imageString;
        }

    }
}
