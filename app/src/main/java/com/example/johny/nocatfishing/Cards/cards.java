package com.example.johny.nocatfishing.Cards;

public class cards {
    private String userId;
    private String name;
    private String profileImageUrl;

    //constructor
    public cards(String userId, String name, String profileImageUrl){
        this.userId =userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }
    //getters and setters
    //for userId
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    //for name
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    //for profileImageURL
    //Glide is used to load images into image from url
    public String getProfileImageUrl(){ return  profileImageUrl;}
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
