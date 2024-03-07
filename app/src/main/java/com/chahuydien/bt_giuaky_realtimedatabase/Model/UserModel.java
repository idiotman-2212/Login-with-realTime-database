package com.chahuydien.bt_giuaky_realtimedatabase.Model;

public class UserModel {

    String userName, email, phone, password;

    public UserModel(String userName, String email, String phone, String password) {
        this.userName = userName;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
