package com.fyp.imranabdulhadi.safehome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

/**
 * Created by Imran Abdulhadi on 10/13/2016.
 **/

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared Preferences
    Editor editor;

    // Context
    Context _context;

    //Shared pref mode
    int PRIVATE_MODE = 0; // 0 - for private mode

    // Shared preferences file name
    private static final String PREF_NAME = "SafeHomePref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name
    public static final String KEY_NAME = "name";

    // Email address
    public static final String KEY_EMAIL = "email";

    // Position
    public static final String KEY_POSITION = "position";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String name, String email, String position) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in preference
        editor.putString(KEY_NAME, name);

        // Storing email in preference
        editor.putString(KEY_EMAIL, email);

        // Storing position
        editor.putString(KEY_POSITION, position);

        // Commit changes
        editor.commit();
    }

    /**
     * CheckLogin method will check user login status
     * If false, will redirect user to the login page
     * else won't do anything
     **/
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // User is not logged in, redirect him to login activity
            Intent intent = new Intent(_context, LoginActivity.class);
            // Closing all the activities
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Starting Login Activity
            _context.startActivity(intent);
        }
    }

    /**
     * Get Stored session data
     **/
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();

        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // user position
        user.put(KEY_POSITION, pref.getString(KEY_POSITION, null));

        return user;
    }

    /**
     * Clear session details
     **/
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout, redirect user to login activity
        Intent intent = new Intent(_context, LoginActivity.class);
        // Closing all the activities
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Starting Login Activity
        _context.startActivity(intent);
    }

    /**
     * Quick check for login status
     * Get login state
     **/
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
