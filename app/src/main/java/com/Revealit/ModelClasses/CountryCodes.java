package com.Revealit.ModelClasses;

import java.util.List;

public class CountryCodes {

    public List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public class Data{

        private String country_code;

        private String country_name;

        private String phone_code;

        private int sort_order;

        private int twilio_support;

        private String flag_url;

        private String mobile_digits;

        private String selected;

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


        public Currency getCurrency() {
            return currency;
        }

        public void setCurrency(Currency currency) {
            this.currency = currency;
        }
    }

    public class Currency {

        private String code;

        private String value;

        private int sort_order;

        private int enabled;

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
