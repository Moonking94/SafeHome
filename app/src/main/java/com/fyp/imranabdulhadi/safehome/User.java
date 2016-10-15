package com.fyp.imranabdulhadi.safehome;

/**
 * Created by Imran Abdulhadi on 10/14/2016.
 */

public class User {
    private String email;
    private String name;
    private String position;

    public User(String email, String name, String position) {
        this.email = email;
        this.name = name;
        this.position = position;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }
}
