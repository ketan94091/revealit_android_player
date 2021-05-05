package com.Revealit.ModelClasses;

import java.util.List;

public class CategoryWisePlayListModel {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }


    public static class DataBean {

        private String categoryName;
        private String slugName;
        private int mediaID;
        private String mediaShowTitle;
        private String mediaTitle;
        private String mediaType;
        private String mediaUrl;
        private String mediaCoverArt;

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getSlugName() {
            return slugName;
        }

        public void setSlugName(String slugName) {
            this.slugName = slugName;
        }


        public int getMediaID() {
            return mediaID;
        }

        public void setMediaID(int mediaID) {
            this.mediaID = mediaID;
        }

        public String getMediaShowTitle() {
            return mediaShowTitle;
        }

        public void setMediaShowTitle(String mediaShowTitle) {
            this.mediaShowTitle = mediaShowTitle;
        }

        public String getMediaTitle() {
            return mediaTitle;
        }

        public void setMediaTitle(String mediaTitle) {
            this.mediaTitle = mediaTitle;
        }

        public String getMediaType() {
            return mediaType;
        }

        public void setMediaType(String mediaType) {
            this.mediaType = mediaType;
        }

        public String getMediaUrl() {
            return mediaUrl;
        }

        public void setMediaUrl(String mediaUrl) {
            this.mediaUrl = mediaUrl;
        }

        public String getMediaCoverArt() {
            return mediaCoverArt;
        }

        public void setMediaCoverArt(String mediaCoverArt) {
            this.mediaCoverArt = mediaCoverArt;
        }
    }
}
