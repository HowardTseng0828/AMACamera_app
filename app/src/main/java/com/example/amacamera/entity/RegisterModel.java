package com.example.amacamera.entity;

public class RegisterModel {

    public String Password, CheckPassword;

    public RegisterModel() {
    }

    public String getUserPassword() {
        return Password;
    }

    public void setUserPassword(String Password) {
        this.Password = Password;
    }

    public String getUserCheckPassword() {
        return CheckPassword;
    }

    public void setUserCheckPassword(String CheckPassword) {
        this.CheckPassword = CheckPassword;
    }
}