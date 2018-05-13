package com.example.siddh.talktry;

public class userDetail {

    private String name;
    private String email;
    private String password;
    private String uid;
    private String dpPicurl;

    public userDetail(String name, String email, String password, String uid, String dpPicurl) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.uid = uid;
        this.dpPicurl = dpPicurl;
    }

    public userDetail() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDpPicurl() {
        return dpPicurl;
    }

    public void setDpPicurl(String dpPicurl) {
        this.dpPicurl = dpPicurl;
    }
}
