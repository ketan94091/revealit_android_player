package com.Revealit.ModelClasses;

import java.util.List;

public class InfluencersModel {

    private List<Data> data = null;
    private Sponsor sponsor;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public Sponsor getSponsor() {
        return sponsor;
    }

    public void setSponsor(Sponsor sponsor) {
        this.sponsor = sponsor;
    }

    public class Data {

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

    public class Sponsor {

        private String influencer_advert_img_url;

        public String getInfluencerAdvertImgUrl() {
            return influencer_advert_img_url;
        }

        public void setInfluencerAdvertImgUrl(String influencer_advert_img_url) {
            this.influencer_advert_img_url = influencer_advert_img_url;
        }

    }
}


