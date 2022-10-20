package com.Revealit.ModelClasses;

public class InviteModel {


        private String tooltip;
        private String currency;
        private String question;
        private  String place_holder;
        private String crypto_currency;
        private String currency_amount;
        private String invitation_message;
        private String call_for_action_amount;
        private  String call_for_action_message;
        private Long campaign_id;
        private Long referral_id;
        private String error;
        private String currency_icon_url;
        private String invitation_message_clipboard;
        private String biometrics_permission_message;

    public String getCurrency_icon_url() {
        return currency_icon_url;
    }

    public void setCurrency_icon_url(String currency_icon_url) {
        this.currency_icon_url = currency_icon_url;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getPlace_holder() {
        return place_holder;
    }

    public void setPlace_holder(String place_holder) {
        this.place_holder = place_holder;
    }

    public String getCrypto_currency() {
        return crypto_currency;
    }

    public void setCrypto_currency(String crypto_currency) {
        this.crypto_currency = crypto_currency;
    }

    public String getCurrency_amount() {
        return currency_amount;
    }

    public void setCurrency_amount(String currency_amount) {
        this.currency_amount = currency_amount;
    }

    public String getInvitation_message() {
        return invitation_message;
    }

    public void setInvitation_message(String invitation_message) {
        this.invitation_message = invitation_message;
    }

    public String getCall_for_action_amount() {
        return call_for_action_amount;
    }

    public void setCall_for_action_amount(String call_for_action_amount) {
        this.call_for_action_amount = call_for_action_amount;
    }

    public String getCall_for_action_message() {
        return call_for_action_message;
    }

    public void setCall_for_action_message(String call_for_action_message) {
        this.call_for_action_message = call_for_action_message;
    }

    public Long getCampaign_id() {
        return campaign_id;
    }

    public void setCampaign_id(Long campaign_id) {
        this.campaign_id = campaign_id;
    }

    public Long getReferral_id() {
        return referral_id;
    }

    public void setReferral_id(Long referral_id) {
        this.referral_id = referral_id;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getInvitation_message_clipboard() {
        return invitation_message_clipboard;
    }

    public void setInvitation_message_clipboard(String invitation_message_clipboard) {
        this.invitation_message_clipboard = invitation_message_clipboard;
    }

    public String getBiometrics_permission_message() {
        return biometrics_permission_message;
    }

    public void setBiometrics_permission_message(String biometrics_permission_message) {
        this.biometrics_permission_message = biometrics_permission_message;
    }
}
