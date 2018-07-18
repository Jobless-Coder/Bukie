package com.example.krishna.bukie;

public class User {
    //private String uid;
    private String profilepic;
    private String signinmethod;
    private String UID;
    private String fullname;

    public User( String profilepic, String signinmethod, String UID, String fullname) {
       // this.uid = uid;
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
        //this.uid = uid;
        this.profilepic = profilepic;
    }

    /*public String getUid() {
        return uid;
    }*/

    public String getProfilepic() {
        return profilepic;
    }
}
