package com.example.krishna.bukie;

public class User {
    private String uid;
    private String fullname;
    private String profilepic;
    private String signinmethod;

    public User(String uid, String fullname, String profilepic, String signinmethod) {
        this.uid = uid;
        this.fullname = fullname;
        this.profilepic = profilepic;
        this.signinmethod = signinmethod;
    }

    public String getUid() {
        return uid;
    }

    public String getFullname() {
        return fullname;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public String getSigninmethod() {
        return signinmethod;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    @Override
    public String toString() {
        return "{" + this.getClass().getCanonicalName() + ": "
                + "uid=" + uid + ", "
                + "fullname=" + fullname + ", "
                + "profilepic=" + profilepic + ", "
                + "signinmethod=" + signinmethod + "}";
    }
}
