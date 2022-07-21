package com.Revealit.ModelClasses;

import java.util.List;

public class ItemListFromItemIdModel {

    public List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public static class Data {

        public int product_id;
        public  String product_name;
        public  String product_thumbnail;

        public int getProduct_id() {
            return product_id;
        }

        public void setProduct_id(int product_id) {
            this.product_id = product_id;
        }

        public String getProduct_name() {
            return product_name;
        }

        public void setProduct_name(String product_name) {
            this.product_name = product_name;
        }

        public String getProduct_thumbnail() {
            return product_thumbnail;
        }

        public void setProduct_thumbnail(String product_thumbnail) {
            this.product_thumbnail = product_thumbnail;
        }
    }
}


