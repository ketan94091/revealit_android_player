package com.Revealit.ModelClasses;

import java.util.List;

public class RewardHistoryDatabaseModel {

    private List<Datum> data = null;

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }


    public static class Datum {

        private String amount;
        private String action;
        private String date;
        private String display_date;
        private Object sponsor;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDisplayDate() {
            return display_date;
        }

        public void setDisplayDate(String display_date) {
            this.display_date = display_date;
        }

        public Object getSponsor() {
            return sponsor;
        }

        public void setSponsor(Object sponsor) {
            this.sponsor = sponsor;
        }

    }

}
