package com.Revealit.ModelClasses;

import java.util.List;

public class NewAuthLoginCallbackModel {

    private String token;
    private String token_type;
    private String status;
    private String is_activated;
    private String role;
    private  int error_code;
    private  String error_msg;
    private List<PublicSetting> public_settings;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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



    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<PublicSetting> getPublic_settings() {
        return public_settings;
    }

    public void setPublic_settings(List<PublicSetting> public_settings) {
        this.public_settings = public_settings;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public class PublicSetting {

        private String os;
        private String api_version;
        private String minimum_acceptable_version;
        private String minimum_acceptable_api_version;
        private String profile_update_reminder_period;
        private String backup_update_reminder_period;
        private String maintenance;
        private AppSetting app_settings;

        public String getOs() {
            return os;
        }

        public void setOs(String os) {
            this.os = os;
        }

        public String getApi_version() {
            return api_version;
        }

        public void setApi_version(String api_version) {
            this.api_version = api_version;
        }

        public String getMinimum_acceptable_version() {
            return minimum_acceptable_version;
        }

        public void setMinimum_acceptable_version(String minimum_acceptable_version) {
            this.minimum_acceptable_version = minimum_acceptable_version;
        }

        public String getMinimum_acceptable_api_version() {
            return minimum_acceptable_api_version;
        }

        public void setMinimum_acceptable_api_version(String minimum_acceptable_api_version) {
            this.minimum_acceptable_api_version = minimum_acceptable_api_version;
        }

        public String getProfile_update_reminder_period() {
            return profile_update_reminder_period;
        }

        public void setProfile_update_reminder_period(String profile_update_reminder_period) {
            this.profile_update_reminder_period = profile_update_reminder_period;
        }

        public String getBackup_update_reminder_period() {
            return backup_update_reminder_period;
        }

        public void setBackup_update_reminder_period(String backup_update_reminder_period) {
            this.backup_update_reminder_period = backup_update_reminder_period;
        }

        public String getMaintenance() {
            return maintenance;
        }

        public void setMaintenance(String maintenance) {
            this.maintenance = maintenance;
        }

        public AppSetting getApp_settings() {
            return app_settings;
        }

        public void setApp_settings(AppSetting app_settings) {
            this.app_settings = app_settings;
        }
    }

    private class AppSetting {

        private List<BlockProducer> block_producers;

        public List<BlockProducer> getBlock_producers() {
            return block_producers;
        }

        public void setBlock_producers(List<BlockProducer> block_producers) {
            this.block_producers = block_producers;
        }
    }

    private class BlockProducer {

        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
