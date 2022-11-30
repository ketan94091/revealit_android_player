package com.Revealit.ModelClasses;

import java.util.List;

public class RevealitHistoryModel {

    public List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public static class Data {

        public int match_id;
        public  int media_id;
        public  String media_title;
        public  String media_type;
        public  String media_url;
        public  String media_cover_art;
        public  String current_time;
        public  String playback_offset;
        public  String playback_display;
        public  String match_time_stamp;
        public  String allTimeStamp;
        public  String allTimeStampOffset;
        public int isSelected = 0;


        public int getMatch_id() {
            return match_id;
        }

        public void setMatch_id(int match_id) {
            this.match_id = match_id;
        }

        public int getMedia_id() {
            return media_id;
        }

        public void setMedia_id(int media_id) {
            this.media_id = media_id;
        }

        public String getMedia_title() {
            return media_title;
        }

        public void setMedia_title(String media_title) {
            this.media_title = media_title;
        }

        public String getMedia_type() {
            return media_type;
        }

        public void setMedia_type(String media_type) {
            this.media_type = media_type;
        }

        public String getMedia_url() {
            return media_url;
        }

        public void setMedia_url(String media_url) {
            this.media_url = media_url;
        }

        public String getMedia_cover_art() {
            return media_cover_art;
        }

        public void setMedia_cover_art(String media_cover_art) {
            this.media_cover_art = media_cover_art;
        }

        public String getCurrent_time() {
            return current_time;
        }

        public void setCurrent_time(String current_time) {
            this.current_time = current_time;
        }

        public String getPlayback_offset() {
            return playback_offset;
        }

        public void setPlayback_offset(String playback_offset) {
            this.playback_offset = playback_offset;
        }

        public String getPlayback_display() {
            return playback_display;
        }

        public void setPlayback_display(String playback_display) {
            this.playback_display = playback_display;
        }

        public String getMatch_time_stamp() {
            return match_time_stamp;
        }

        public void setMatch_time_stamp(String match_time_stamp) {
            this.match_time_stamp = match_time_stamp;
        }

        public String getAllTimeStamp() {
            return allTimeStamp;
        }

        public void setAllTimeStamp(String allTimeStamp) {
            this.allTimeStamp = allTimeStamp;
        }

        public String getAllTimeStampOffset() {
            return allTimeStampOffset;
        }

        public void setAllTimeStampOffset(String allTimeStampOffset) {
            this.allTimeStampOffset = allTimeStampOffset;
        }

        public int getIsSelected() {
            return isSelected;
        }

        public void setIsSelected(int isSelected) {
            this.isSelected = isSelected;
        }
    }
}


