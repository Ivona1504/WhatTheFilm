package fer.ppij.whatthefilm.model;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String uid;
    private String username;
    private String email;
    private String firstname;
    private String lastname;
    private int age;
    private Map<String, String> friends;

    public User() {}

    public User(String uid, String email, String username) {
        this.uid = uid;
        this.email = email;
        this.username = username;
        firstname = "Name";
        lastname = "Lastname";
        friends = new HashMap<>();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Map<String, String> getFriends() {
        return friends;
    }

    public void setFriends(HashMap<String, String> friends) {
        this.friends = friends;
    }
}
