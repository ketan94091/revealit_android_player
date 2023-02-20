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
        String mobileNumber;
        int serverInstanceId;
        int isAccountRemoved ;
        SubmitProfileModel submitProfileModel;
        private UserProfile userProfile;

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

        public String getMobileNumber() {
            return mobileNumber;
        }

        public void setMobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
        }

        public SubmitProfileModel getSubmitProfileModel() {
            return submitProfileModel;
        }

        public void setSubmitProfileModel(SubmitProfileModel submitProfileModel) {
            this.submitProfileModel = submitProfileModel;
        }

        public int getIsAccountRemoved() {
            return isAccountRemoved;
        }

        public void setIsAccountRemoved(int isAccountRemoved) {
            this.isAccountRemoved = isAccountRemoved;
        }

        public UserProfile getUserProfile() {
            return userProfile;
        }

        public void setUserProfile(UserProfile userProfile) {
            this.userProfile = userProfile;
        }
    }
    private class UserProfile {
        private int id;
        private String user_id;
        private String name;
        private String first_name;
        private String last_name;
        private String email;
        private String date_of_birth;
        private String gender;
        private String profile_image;
        private String account_type;
        private String classification;
        private String audience;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getLast_name() {
            return last_name;
        }

        public void setLast_name(String last_name) {
            this.last_name = last_name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getDate_of_birth() {
            return date_of_birth;
        }

        public void setDate_of_birth(String date_of_birth) {
            this.date_of_birth = date_of_birth;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getProfile_image() {
            return profile_image;
        }

        public void setProfile_image(String profile_image) {
            this.profile_image = profile_image;
        }

        public String getAccount_type() {
            return account_type;
        }

        public void setAccount_type(String account_type) {
            this.account_type = account_type;
        }

        public String getClassification() {
            return classification;
        }

        public void setClassification(String classification) {
            this.classification = classification;
        }

        public String getAudience() {
            return audience;
        }

        public void setAudience(String audience) {
            this.audience = audience;
        }
    }
}
