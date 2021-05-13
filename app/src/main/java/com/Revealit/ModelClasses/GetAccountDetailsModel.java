package com.Revealit.ModelClasses;

import java.util.List;

public class GetAccountDetailsModel {

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        private String account_name;
        private List<Token> tokens = null;

        public String getAccountName() {
            return account_name;
        }

        public void setAccountName(String account_name) {
            this.account_name = account_name;
        }

        public List<Token> getTokens() {
            return tokens;
        }

        public void setTokens(List<Token> tokens) {
            this.tokens = tokens;
        }

    }

    public static class Token {

        private String token_icon;
        private String symbol;
        private Integer precision;
        private float amount;
        private String contract;
        private String in_usd;
        private String in_usd_icon;
        private String in_btc;
        private String in_btc_icon;
        private List<Values> values = null;

        public List<Values> getValues() {
            return values;
        }

        public void setValues(List<Values> values) {
            this.values = values;
        }

        public String getTokenIcon() {
            return token_icon;
        }

        public void setTokenIcon(String token_icon) {
            this.token_icon = token_icon;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public Integer getPrecision() {
            return precision;
        }

        public void setPrecision(Integer precision) {
            this.precision = precision;
        }

        public float getAmount() {
            return amount;
        }

        public void setAmount(float amount) {
            this.amount = amount;
        }

        public String getContract() {
            return contract;
        }

        public void setContract(String contract) {
            this.contract = contract;
        }

        public String getInUsd() {
            return in_usd;
        }

        public void setInUsd(String in_usd) {
            this.in_usd = in_usd;
        }

        public String getInUsdIcon() {
            return in_usd_icon;
        }

        public void setInUsdIcon(String in_usd_icon) {
            this.in_usd_icon = in_usd_icon;
        }

        public String getInBtc() {
            return in_btc;
        }

        public void setInBtc(String in_btc) {
            this.in_btc = in_btc;
        }

        public String getInBtcIcon() {
            return in_btc_icon;
        }

        public void setInBtcIcon(String in_btc_icon) {
            this.in_btc_icon = in_btc_icon;
        }


        public static class Values {

            private String symbol;
            private String value;
            private String icon;
            private String name;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getSymbol() {
                return symbol;
            }

            public void setSymbol(String symbol) {
                this.symbol = symbol;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }
        }

    }




}


