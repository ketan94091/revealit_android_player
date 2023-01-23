package com.Revealit.ModelClasses;

public class UserDetailsFromPublicKeyModel {

    String status;
    String revealit_private_key;
    String token;
    String role;
    String is_activated;
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
        return token;
    }

    public void setAuth_token(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getIs_activated() {
        return is_activated;
    }

    public void setIs_activated(String is_activated) {
        this.is_activated = is_activated;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }
}
