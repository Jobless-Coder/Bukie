package com.example.krishna.bukie;

public class User {
    private String username;
    private String profilepic;
    private String signinmethod;
    private String UID;
    private String fullname;

    public User(String username, String profilepic, String signinmethod, String UID, String fullname) {
        this.username = username;
        this.profilepic = profilepic;
        this.signinmethod = signinmethod;
        this.UID = UID;
        this.fullname = fullname;
    }

    public String getFullname() {
        return fullname;
    }

    public String getUID() {
        return UID;
    }



    public String getSigninmethod() {
        return signinmethod;

    }

   public User(String username, String profilepic) {
        this.username = username;
        this.profilepic = profilepic;
    }

    public String getUsername() {
        return username;
    }

    public String getProfilepic() {
        return profilepic;
    }
}
