package com.Revealit.ModelClasses;

public class UserDetailsFromPublicKeyModel {

    String status;
    String revealit_private_key;
    String auth_token;
    String role;
    int is_activated;
    String audience;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRevealit_private_key() {
        return revealit_private_key;
    }

    public void setRevealit_private_key(String revealit_private_key) {
        this.revealit_private_key = revealit_private_key;
    }

    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getIs_activated() {
        return is_activated;
    }

    public void setIs_activated(int is_activated) {
        this.is_activated = is_activated;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }
}
