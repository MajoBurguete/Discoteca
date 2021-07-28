package com.example.discoteca.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Fact")
public class Fact  extends ParseObject {

    public static final String KEY_USER = "userFact";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_ID = "songId";
    public static final String KEY_URL = "url";
    public static final String KEY_SONG = "song";
    public static final String KEY_ALBUM = "album";
    public static final String KEY_ARTIST = "artist";
    public static final String KEY_LIKES = "likes";

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

    public String getId() {
        return getString(KEY_ID);
    }

    public void setId(String id){
        put(KEY_ID, id);
    }

    public String getUrl(){ return getString(KEY_URL); }

    public void setUrl(String url){ put(KEY_URL, url); }

    public String getSong(){ return getString(KEY_SONG); }

    public void setSong(String song){ put(KEY_SONG, song); }

    public String getAlbum(){ return getString(KEY_ALBUM); }

    public void setAlbum(String album){ put(KEY_ALBUM, album); }

    public String getArtist(){ return getString(KEY_ARTIST); }

    public void setArtist(String artist){ put(KEY_ARTIST, artist); }

    public int getLikes(){ return getInt(KEY_LIKES); }

    public void setLikes(int likes){ put(KEY_LIKES, likes); }
}
