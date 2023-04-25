package com.Revealit.ModelClasses;

import java.util.List;

public class SavedProductListModel {


    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public  class Data{
        private Long id;
        private String name;
        private Long order;
        private boolean isSelected = false;

            private List<Item> items;

            public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getOrder() {
            return order;
        }

        public void setOrder(Long order) {
            this.order = order;
        }


        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
        }


        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }
    }

    public class Item {

         private Long item_id;
         private String product_name;
         private BestOffer best_offer;
         private String image;

        public Long getItem_id() {
            return item_id;
        }

        public void setItem_id(Long item_id) {
            this.item_id = item_id;
        }

        public String getProduct_name() {
            return product_name;
        }

        public void setProduct_name(String product_name) {
            this.product_name = product_name;
        }


        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public BestOffer getBest_offer() {
            return best_offer;
        }

        public void setBest_offer(BestOffer best_offer) {
            this.best_offer = best_offer;
        }
    }

    private class BestOffer {

        private String price;
        private Long offer_id;
        private String offer_url;
        private String offer_text;
        private String price_currency;

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public Long getOffer_id() {
            return offer_id;
        }

        public void setOffer_id(Long offer_id) {
            this.offer_id = offer_id;
        }

        public String getOffer_url() {
            return offer_url;
        }

        public void setOffer_url(String offer_url) {
            this.offer_url = offer_url;
        }

        public String getOffer_text() {
            return offer_text;
        }

        public void setOffer_text(String offer_text) {
            this.offer_text = offer_text;
        }

        public String getPrice_currency() {
            return price_currency;
        }

        public void setPrice_currency(String price_currency) {
            this.price_currency = price_currency;
        }
    }
}

