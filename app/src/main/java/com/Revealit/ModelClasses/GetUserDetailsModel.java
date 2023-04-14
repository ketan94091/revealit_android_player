package com.Revealit.ModelClasses;

public class GetUserDetailsModel {

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

}
class Data {

    private Integer id;
    private String user_id;
    private String name;
    private Object first_name;
    private Object last_name;
    private Object email;
    private String date_of_birth;
    private Object gender;
    private String profile_image;
    private String account_type;
    private String classification;
    private String audience;
    private String revealit_private_key;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Object getFirst_name() {
        return first_name;
    }

    public void setFirst_name(Object first_name) {
        this.first_name = first_name;
    }

    public Object getLast_name() {
        return last_name;
    }

    public void setLast_name(Object last_name) {
        this.last_name = last_name;
    }

    public Object getEmail() {
        return email;
    }

    public void setEmail(Object email) {
        this.email = email;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public Object getGender() {
        return gender;
    }

    public void setGender(Object gender) {
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

    public String getRevealit_private_key() {
        return revealit_private_key;
    }

    public void setRevealit_private_key(String revealit_private_key) {
        this.revealit_private_key = revealit_private_key;
    }
}

