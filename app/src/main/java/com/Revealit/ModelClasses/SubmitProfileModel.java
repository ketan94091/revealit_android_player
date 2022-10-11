package com.Revealit.ModelClasses;

public class SubmitProfileModel {

        private Boolean status;
        private String revealit_private_key;
        private String role;
        private String auth_token;
        private String audience;
        private Proton proton;
        private boolean activated;

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public Boolean getSuccess() {
            return status;
        }

        public void setSuccess(Boolean success) {
            this.status = success;
        }

        public String getrevealit_private_key() {
            return revealit_private_key;
        }

        public void setrevealit_private_key(String revealit_private_key) {
            this.revealit_private_key = revealit_private_key;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getauth_token() {
            return auth_token;
        }

        public void setauth_token(String auth_token) {
            this.auth_token = auth_token;
        }

        public String getAudience() {
            return audience;
        }

        public void setAudience(String audience) {
            this.audience = audience;
        }

        public Proton getProton() {
            return proton;
        }

        public void setProton(Proton proton) {
            this.proton = proton;
        }


    public class Proton {

        private String account_name;
        private String public_key;
        private String private_key;
        private String mnemonic;

        public String getAccountName() {
            return account_name;
        }

        public void setAccountName(String accountName) {
            this.account_name = accountName;
        }

        public String getPublicKey() {
            return public_key;
        }

        public void setPublicKey(String publicKey) {
            this.public_key = publicKey;
        }

        public String getPrivateKey() {
            return private_key;
        }

        public void setPrivateKey(String privateKey) {
            this.private_key = privateKey;
        }

        public String getMnemonic() {
            return mnemonic;
        }

        public void setMnemonic(String mnemonic) {
            this.mnemonic = mnemonic;
        }

    }
}