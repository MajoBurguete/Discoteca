package com.example.discoteca.models;

import org.parceler.Parcel;

@Parcel
public class Song<type> {

    private String songId;
    private String artistName;
    private String songName;
    private String albumName;
    private String releaseDate;
    private long duration;
    private String imageUrl;

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setArtistName(String artist){
        artistName = artist;
    }

    public String getArtistName(){
        return artistName;
    }

    public String getType() {
        return "song";
    }

}
