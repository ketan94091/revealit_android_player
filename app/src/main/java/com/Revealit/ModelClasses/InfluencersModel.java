package com.Revealit.ModelClasses;

import java.util.List;

public class InfluencersModel {

    private List<Data> data = null;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public class Data{

        private String name;
        private String bio;
        private String influencer_image;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }

        public String getInfluencer_image() {
            return influencer_image;
        }

        public void setInfluencer_image(String influencer_image) {
            this.influencer_image = influencer_image;
        }
    }
}


