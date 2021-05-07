package com.Revealit.ModelClasses;

public class UserRegistrationModel {

    private String status;
    private String message;
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public class Data {

        private String access_token;
        private String token_type;
        private String expires_in;
        private String proton_account_name;

        public String getAccessToken() {
            return access_token;
        }

        public void setAccessToken(String access_token) {
            this.access_token = access_token;
        }

        public String getTokenType() {
            return token_type;
        }

        public void setTokenType(String token_type) {
            this.token_type = token_type;
        }

        public String getExpiresIn() {
            return expires_in;
        }

        public void setExpiresIn(String expires_in) {
            this.expires_in = expires_in;
        }

        public String getProtonAccountName() {
            return proton_account_name;
        }

        public void setProtonAccountName(String proton_account_name) {
            this.proton_account_name = proton_account_name;
        }

    }
}


