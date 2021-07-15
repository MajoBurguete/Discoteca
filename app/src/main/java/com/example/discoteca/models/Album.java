package com.example.discoteca.models;

public class Album {

    private String albumId;
    private String artistName;
    private String albumName;
    private int noTracks;
    private String releaseDate;
    private String imageUrl;

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public int getNoTracks() {
        return noTracks;
    }

    public void setNoTracks(int noTracks) {
        this.noTracks = noTracks;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
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
        return "album";
    }
    
}
