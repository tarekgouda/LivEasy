package cse110.liveasy;

/**
 *User class is the class that represents the main user, which is used to populate the user info
 *upon signing up or login in
 */

public class User {
    public String username;
    public String full_name;
    public String phone_number;
    public String email;
    public boolean group;
    public boolean isPending;
    public String groupID;
    public String photo_url;


    public User(String full_name, String phone_number, String email, boolean group) {
        this.full_name = full_name;
        this.phone_number = phone_number;
        this.email = email;
        this.group = group;
        this.groupID = "";
        this.isPending = false;
    }

    public User() {
    }

    public String getUsername(){
        return username;
    }
    public String getFull_name(){
        return full_name;
    }
    public String getPhone_number(){
        return phone_number;
    }
    public String getEmail(){
        return email;
    }
    public boolean getGroup(){
        return group;
    }
    public String getPhotoUrl() { return photo_url; }

}
