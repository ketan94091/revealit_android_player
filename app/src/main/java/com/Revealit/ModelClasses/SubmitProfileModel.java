package com.Revealit.ModelClasses;

public class SubmitProfileModel {

        private String status;
        private String revealit_private_key;
        private String role;
        private String token;
        private String audience;
        private Proton proton;
        private String is_activated;
        private String message;
        private int error_code;
        private String serverInstance;


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
            return token;
        }

        public void setauth_token(String token) {
            this.token = token;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getServerInstance() {
        return serverInstance;
    }

    public void setServerInstance(String serverInstance) {
        this.serverInstance = serverInstance;
    }

    public static  class Proton {

        private String account_name;
        private String public_key;
        private String private_key;
        private String mnemonic;
        private String public_pem;
        private String private_pem;

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

        public String getPublic_pem() {
            return public_pem;
        }

        public void setPublic_pem(String public_pem) {
            this.public_pem = public_pem;
        }

        public String getPrivate_pem() {
            return private_pem;
        }

        public void setPrivate_pem(String private_pem) {
            this.private_pem = private_pem;
        }
    }
}