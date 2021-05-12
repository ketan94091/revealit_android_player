package com.Revealit.ModelClasses;

import java.util.List;

public class RewardHistoryModel {

    private List<Datum> data = null;
    private Links links;
    private Meta meta;

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public class Datum {

        private float amount;
        private String action;
        private String date;
        private String display_date;
        private Object sponsor;

        public float getAmount() {
            return amount;
        }

        public void setAmount(float amount) {
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

    public class Link {

        private String url;
        private String label;
        private Boolean active;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }

    }

    public class Links {

        private String first;
        private String last;
        private Object prev;
        private String next;

        public String getFirst() {
            return first;
        }

        public void setFirst(String first) {
            this.first = first;
        }

        public String getLast() {
            return last;
        }

        public void setLast(String last) {
            this.last = last;
        }

        public Object getPrev() {
            return prev;
        }

        public void setPrev(Object prev) {
            this.prev = prev;
        }

        public String getNext() {
            return next;
        }

        public void setNext(String next) {
            this.next = next;
        }

    }

    public class Meta {

        private Integer current_page;
        private Integer from;
        private Integer last_page;
        private List<Link> links = null;
        private String path;
        private Integer per_page;
        private Integer to;
        private Integer total;

        public Integer getCurrentPage() {
            return current_page;
        }

        public void setCurrentPage(Integer current_page) {
            this.current_page = current_page;
        }

        public Integer getFrom() {
            return from;
        }

        public void setFrom(Integer from) {
            this.from = from;
        }

        public Integer getLastPage() {
            return last_page;
        }

        public void setLastPage(Integer last_page) {
            this.last_page = last_page;
        }

        public List<Link> getLinks() {
            return links;
        }

        public void setLinks(List<Link> links) {
            this.links = links;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public Integer getPerPage() {
            return per_page;
        }

        public void setPerPage(Integer per_page) {
            this.per_page = per_page;
        }

        public Integer getTo() {
            return to;
        }

        public void setTo(Integer to) {
            this.to = to;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }


    }
}
