package com.example.discoteca.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Fact")
public class Fact  extends ParseObject {

    public static final String KEY_USER = "userFact";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_SONG = "songId";

}
