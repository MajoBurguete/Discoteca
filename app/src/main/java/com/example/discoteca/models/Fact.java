package com.example.discoteca.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Fact")
public class Fact  extends ParseObject {

    public static final String KEY_USER = "userFact";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_SONG = "songId";

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }

    public String getSong() {
        return getString(KEY_SONG);
    }

    public void setSong(String id){
        put(KEY_SONG, id);
    }
}
