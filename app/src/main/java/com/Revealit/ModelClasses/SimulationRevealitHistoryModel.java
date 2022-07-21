package com.Revealit.ModelClasses;

import java.util.List;

public class SimulationRevealitHistoryModel {

    public List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public static class Data {

        public int intId;
        public String mediaURL;
        public int mediaId;
        public String mediaTitle;
        public String mediaCoverArt;
        public String playbackDisplayOffset;
        public String playbackOffset;


        public int getIntId() {
            return intId;
        }

        public void setIntId(int intId) {
            this.intId = intId;
        }

        public String getMediaURL() {
            return mediaURL;
        }

        public void setMediaURL(String mediaURL) {
            this.mediaURL = mediaURL;
        }

        public int getMediaId() {
            return mediaId;
        }

        public void setMediaId(int mediaId) {
            this.mediaId = mediaId;
        }

        public String getPlaybackDisplayOffset() {
            return playbackDisplayOffset;
        }

        public void setPlaybackDisplayOffset(String playbackDisplayOffset) {
            this.playbackDisplayOffset = playbackDisplayOffset;
        }

        public String getPlaybackOffset() {
            return playbackOffset;
        }

        public void setPlaybackOffset(String playbackOffset) {
            this.playbackOffset = playbackOffset;
        }


        public String getMediaTitle() {
            return mediaTitle;
        }

        public void setMediaTitle(String mediaTitle) {
            this.mediaTitle = mediaTitle;
        }

        public String getMediaCoverArt() {
            return mediaCoverArt;
        }

        public void setMediaCoverArt(String mediaCoverArt) {
            this.mediaCoverArt = mediaCoverArt;
        }
    }
}


