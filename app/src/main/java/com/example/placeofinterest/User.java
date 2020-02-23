package com.example.placeofinterest;

public class User {

    private String username;
    private String email;
    private String password;
    private String  phoneNumber;
    private String gender;
    private String  isNew;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String password, String phoneNumber, String gender, String  isNew) {

        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.isNew = isNew;
    }




    public String  getisnew() {
        return isNew;
    }

    public void setisnew(String isNew) {
        this.isNew = isNew;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}