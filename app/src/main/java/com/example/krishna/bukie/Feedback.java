package com.example.krishna.bukie;

public class Feedback {

private String userID;
private String review;
private String imageUrl;


    public Feedback(String user, String rev, String url)
    {
        userID = user;
        review = rev;
        imageUrl = url;
    }

    public Feedback()
    {

    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
