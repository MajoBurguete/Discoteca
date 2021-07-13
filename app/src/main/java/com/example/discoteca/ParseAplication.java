package com.example.discoteca;

import android.app.Application;

import com.parse.Parse;

public class ParseAplication extends Application {

    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("hxXJbnrjYSxmhGpdDo748j4TT86dCJTJ1KT7i8Bz")
                .clientKey("SR0vSvFbKkQ23ybTgZt0JgBfKX8CnhV5GHxBsHYb")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }

}
