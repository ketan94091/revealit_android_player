package com.Revealit.ModelClasses;

import java.util.List;

public class KeyStoreServerInstancesModel {

    public List<Data> data;
    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public static class Data {

        String serverInstanceName;
        int serverInstanceId;
        SubmitProfileModel submitProfileModel;

        public String getServerInstanceName() {
            return serverInstanceName;
        }

        public void setServerInstanceName(String serverInstanceName) {
            this.serverInstanceName = serverInstanceName;
        }

        public int getServerInstanceId() {
            return serverInstanceId;
        }

        public void setServerInstanceId(int serverInstanceId) {
            this.serverInstanceId = serverInstanceId;
        }

        public SubmitProfileModel getSubmitProfileModel() {
            return submitProfileModel;
        }

        public void setSubmitProfileModel(SubmitProfileModel submitProfileModel) {
            this.submitProfileModel = submitProfileModel;
        }

    }
}
