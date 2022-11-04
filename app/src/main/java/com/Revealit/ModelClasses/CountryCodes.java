package com.Revealit.ModelClasses;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import java.io.Serializable;
import java.util.List;

public class CountryCodes implements Serializable {

    @SerializedName("data")
    @NotNull
    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    private class Data implements Serializable{

        @SerializedName("country_code")
        @NotNull
        private String country_code;

        @SerializedName("country_name")
        @NotNull
        private String country_name;

        @SerializedName("phone_code")
        @NotNull
        private String phone_code;

        @SerializedName("sort_order")
        @NotNull
        private int sort_order;

        @SerializedName("twilio_support")
        @NotNull
        private int twilio_support;

        @SerializedName("flag_url")
        @NotNull
        private String flag_url;

        @SerializedName("mobile_digits")
        @NotNull
        private String mobile_digits;

        @SerializedName("selected")
        @NotNull
        private String selected;

        @SerializedName("currency")
        @Nullable
        private Currency currency;

        public String getCountry_code() {
            return country_code;
        }

        public void setCountry_code(String country_code) {
            this.country_code = country_code;
        }

        public String getCountry_name() {
            return country_name;
        }

        public void setCountry_name(String country_name) {
            this.country_name = country_name;
        }

        public String getPhone_code() {
            return phone_code;
        }

        public void setPhone_code(String phone_code) {
            this.phone_code = phone_code;
        }

        public int getSort_order() {
            return sort_order;
        }

        public void setSort_order(int sort_order) {
            this.sort_order = sort_order;
        }

        public int getTwilio_support() {
            return twilio_support;
        }

        public void setTwilio_support(int twilio_support) {
            this.twilio_support = twilio_support;
        }

        public String getFlag_url() {
            return flag_url;
        }

        public void setFlag_url(String flag_url) {
            this.flag_url = flag_url;
        }

        public String getMobile_digits() {
            return mobile_digits;
        }

        public void setMobile_digits(String mobile_digits) {
            this.mobile_digits = mobile_digits;
        }

        public String getSelected() {
            return selected;
        }

        public void setSelected(String selected) {
            this.selected = selected;
        }

        @Nullable
        public Currency getCurrency() {
            return currency;
        }

        public void setCurrency(@Nullable Currency currency) {
            this.currency = currency;
        }
    }

    private class Currency  implements Serializable{

        @SerializedName("code")
        @Nullable
        private String code;

        @SerializedName("value")
        @Nullable
        private String value;

        @SerializedName("sort_order")
        @Nullable
        private int sort_order;

        @SerializedName("enabled")
        @Nullable
        private int enabled;

        @SerializedName("currency_icon_url")
        @Nullable
        private String currency_icon_url;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getSort_order() {
            return sort_order;
        }

        public void setSort_order(int sort_order) {
            this.sort_order = sort_order;
        }

        public int getEnabled() {
            return enabled;
        }

        public void setEnabled(int enabled) {
            this.enabled = enabled;
        }

        public String getCurrency_icon_url() {
            return currency_icon_url;
        }

        public void setCurrency_icon_url(String currency_icon_url) {
            this.currency_icon_url = currency_icon_url;
        }
    }
}
