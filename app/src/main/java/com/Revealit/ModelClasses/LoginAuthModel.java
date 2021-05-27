package com.Revealit.ModelClasses;

public class LoginAuthModel {

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

    public String getProton_account_name() {
        return proton_account_name;
    }

    public void setProton_account_name(String proton_account_name) {
        this.proton_account_name = proton_account_name;
    }
}
