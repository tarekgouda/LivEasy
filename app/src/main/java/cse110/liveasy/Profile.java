package cse110.liveasy;


import java.util.Map;

/**
 * Created by Duke Lin on 10/30/2016.
 *Profile object class that creates an Profile for each user in the group
 *
 */

public class Profile {

    String username;
    String email;
    String phoneNum;
    String photo_url;

    public Profile(Map<String, Object> user, String username) {

        this.username = username;
        this.email = (String)user.get("email");
        this.phoneNum = (String)user.get("phone_number");
        this.photo_url = (String)user.get("photo_url");
    }

    public Profile(User userObject, String username) {

        this.username = username;
        this.email = userObject.getEmail();
        this.phoneNum = userObject.getPhone_number();
        this.photo_url = userObject.getPhotoUrl();

    }
}
