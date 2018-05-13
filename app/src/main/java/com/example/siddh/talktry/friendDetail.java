package com.example.siddh.talktry;

public class friendDetail {

    private String name;
    private String profilePicURi;
    private String email;

    public friendDetail(String name, String profilePicURi, String email) {
        this.name = name; this.profilePicURi = profilePicURi; this.email = email;
    }

    public friendDetail() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePicURi() {
        return profilePicURi;
    }

    public void setProfilePicURi(String profilePicURi) {
        this.profilePicURi = profilePicURi;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
