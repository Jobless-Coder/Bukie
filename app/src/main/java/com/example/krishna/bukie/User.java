package com.example.krishna.bukie;

public class User {
    private String username;
    private String profilepic;
    private String signinmethod;

    public User(String username, String profilepic, String signinmethod) {
        this.username = username;
        this.profilepic = profilepic;
        this.signinmethod = signinmethod;
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
