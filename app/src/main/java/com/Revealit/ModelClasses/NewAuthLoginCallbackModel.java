package com.Revealit.ModelClasses;

public class NewAuthLoginCallbackModel {

    private String auth_token;
    private String token_type;
    private String status;
    private String is_activated;
    private String role;
    private PublicSetting public_settings;

    public String getToken() {
        return auth_token;
    }

    public void setToken(String auth_token) {
        this.auth_token = auth_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIs_activated() {
        return is_activated;
    }

    public void setIs_activated(String is_activated) {
        this.is_activated = is_activated;
    }

    public PublicSetting getPuplic_settings() {
        return public_settings;
    }

    public void setPuplic_settings(PublicSetting public_settings) {
        this.public_settings = public_settings;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public PublicSetting getPublic_settings() {
        return public_settings;
    }

    public void setPublic_settings(PublicSetting public_settings) {
        this.public_settings = public_settings;
    }

    public class PublicSetting {

      private String minumum_acceptable_version;
      private String minumum_acceptable_api_version;
      private int profile_update_reminder_period;
      private int backup_update_reminder_period;

        public String getMinumum_acceptable_version() {
            return minumum_acceptable_version;
        }

        public void setMinumum_acceptable_version(String minumum_acceptable_version) {
            this.minumum_acceptable_version = minumum_acceptable_version;
        }

        public String getMinumum_acceptable_api_version() {
            return minumum_acceptable_api_version;
        }

        public void setMinumum_acceptable_api_version(String minumum_acceptable_api_version) {
            this.minumum_acceptable_api_version = minumum_acceptable_api_version;
        }

        public int getProfile_update_reminder_period() {
            return profile_update_reminder_period;
        }

        public void setProfile_update_reminder_period(int profile_update_reminder_period) {
            this.profile_update_reminder_period = profile_update_reminder_period;
        }

        public int getBackup_update_reminder_period() {
            return backup_update_reminder_period;
        }

        public void setBackup_update_reminder_period(int backup_update_reminder_period) {
            this.backup_update_reminder_period = backup_update_reminder_period;
        }
    }
}