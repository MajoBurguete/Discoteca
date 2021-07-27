# Discoteca

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Discoteca is an app that allows you to add facts about a song or about the artist.

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Entertainment
- **Mobile:** This app would be primarily developed for mobile but making it available for computer could be taken into consideration in a future. 
- **Story:** Allows users to know and to add random facts about a song artist or album.
- **Market:** Anyone who likes knowing about music industry or song writing.
- **Habit:** Could be used ocassionally at any time.
- **Scope:** First we would start as a platform to see and share facts about songs/artist/albums but in a future it could be a social networking platform where you can meet people with similar music taste.

## Product Spec

**Required Must-have Stories**

**Required Must-have Stories**

- [x] User can login and logout
- [x] User can sign up
- [x] User can search for any song/album that's on Spotify.
- [x] User can see the songs on the album chosed 
- [x] User can choose the song they want to write a fact about.
- [x] User can choose a song and see all of the facts that's ever got.
- [x] User can publish a fact.
- [x] User can see different facts about random songs in their home screen.
- [x] User can refresh their home screen to see if any other fact was published.
- [ ] The app uses animations
- [x] The user session stays open until the user logs out (even if th app was closed).
- [x] User can update their username
- [x] User can see the number of posts they have made
- [x] User can see the posts they have made
- [x] User can delete a fact made by them

**Optional Nice-to-have Stories**

- [ ] User can update their email
- [x] Infinite scrolling is implemented on those activities that make requests
- [x] User can signup with a profile picture
- [x] User can update their profile picture
- [ ] User can search for any artist and see their albums
- [x] User can like or dislike a fact
- [x] User can see all of the facts liked by them in their profile
- [ ] User can see the song's lyrics.
- [ ] Facts on the home screen will only be about the top artist of the user.

### 2. Screen Archetypes

* Login
   * User can log in into their account
   
* Signup
   * User can signup

* Home
   * User can see different facts about random songs
   * User can refresh the scree and see the new facts that were added.

* Compose
   * User can choose the song they want to write a fact about

* Search
   * User can search for any song by the name or album that's on spotify
   * User can choose a song/album and see details about it.

* Profile
   * User can see the number of posts they have made
   * User can see the posts they have made
   * User can delete a fact made by them

* Settings
   * User can logout 
   * User can update their username

* Album Detail
   * User can see details about the album
   * User can see the songs on the album
   * User can choose a song to see the details
   
* Song Detail 
   * User can see all of the facts that people have ever added.


### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home Screen
* Search Screen
* Compose Screen
* Profile Screen

**Flow Navigation** (Screen to Screen)

* Search Screen
   * Album/Song detail
   
* Album Detail
   * Song detail
  
* Song Detail
   * Facts feed where the user can also compose a new fact.  

* Profile Screen
   * Settings where the user can update their username and logout 
   
* Compose Screen
   * Access album/song  


## Wireframes
<img src="https://github.com/MajoBurguete/Discoteca/blob/master/UI/WireframeT.png" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype


## Schema 

### Models

#### User

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user post (default field) |
   | username      | String   | Username inside the app |
   | password      | String   | Password of the account |
   | email         | String.  | User's Email address |
   | profilePic    | File     | User's profile picture |
   | factsLiked    | Array    | All of the facts ever liked |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |

#### Fact

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user post (default field) |
   | user          | Pointer to User| author of the fact |
   | description   | String   | Fact about the song or artist |
   | songId        | String   | Song id gotten from the spotify API |
   | likesCount    | Number   | number of likes for the post |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |

### 


### Networking

#### List of network requests by screen
   - Sign Up Screen
      - Sign Up request
       ```java
        user.signUpInBackground(new SignUpCallback() {
           @Override
           public void done(ParseException e) {
               if (e != null) {
                   Toast.makeText(SignupActivity.this, "Failed to signup, try again!", Toast.LENGTH_LONG).show();
                   Log.e(TAG, "Failed to signup", e);
                   return;
               }

               // save profile pic first
               ParseUser user = ParseUser.getCurrentUser();
               user.put(KEY_PROFILE, photoFile);
               user.saveInBackground(new SaveCallback() {
                   @Override
                   public void done(ParseException e) {
                       // go back to previous activity
                       Intent result = new Intent();
                       result.putExtra("username", username);
                       result.putExtra("password", password);
                       setResult(RESULT_OK, result);
                       finish();
                   }
               });
           }
        });
        ```
   - Home Feed Screen
      - (Read/GET) Query all facts in the database
         ```java
         ParseQuery<Fact> query = ParseQuery.getQuery(Fact.class);
         query.order(byDescending: "createdAt");
         query.findInBackground(new FindCallback<Fact>() {
            @Override
            public void done(List<Fact> facts, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting facts", e);
                    return;
                }
                // Add all the facts found to the facts list 
            }
        }
         ```
      - (Create/POST) Create a new like on a post
      - (Delete) Delete existing like
      - (Delete) Delete existing fact
         ```java
         ParseQuery<Fact> query = ParseQuery.getQuery(Fact.class);
        // Query parameters based on the item name
         query.whereEqualTo("objectId", "QHjRWwgEtd");
         query.findInBackground(new FindCallback<Fact>() {
             @Override
             public void done(final List<Facts> fact, ParseException e) {
                if (e == null) {
                  fact.get(0).deleteInBackground(new DeleteCallback() {
                  @Override
                  public void done(ParseException e) {
                      if (e == null) {
                        // Success
                      } else {
                        // Failed
                      }
                  }
                 });
                } else {
                  // Something is wrong
                }
             };
        }
         ```
   - Create Post Screen
       - (Create/POST) Create a new fact about a song/artist/album
        ```java
         Fact fact = new Fact();
         fact.put(KEY_SONG, factReceived.song);
         fact.put(KEY_ALBUM, factReceived.album);
         fact.put(KEY_ARTIST, factReceived.artist);
         fact.put(KEY_USER, factReceived.getUser());
         fact.put(KEY_DESCRIPTION, factReceived.description);
         fact.saveInBackground(new SaveCallback(){
             @Override
             public void done(ParseException e){
               if(e == null){
                 // Succes
               } else{
                 // Error
               }
             }
         });
         ```
   - Profile Screen
      - (Read/GET) Query logged in user object
        ```java
        ParseUser.getCurrentUser();
        ```
      - (Read/GET) Query facts made by the user
        ```java
        ParseQuery<Fact> query = ParseQuery.getQuery(Fact.class);
        query.include(Fact.KEY_USER);
        query.whereEqualTo(Fact.KEY_USER, ParseUser.getCurrentUser());
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Fact>() {
            @Override
            public void done(List<Fact> facts, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for (Post fact : facts){
                    Log.i(TAG, "Post: " + post.getDescription() + ", Username: " + post.getUser().getUsername());
                }
                // Add all the facts found to the facts list
            }
        });
        ```
      - (Read/GET) Query facts liked by the user
      - (Update/PUT) Update user profile image
        ```java
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser);
        query.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>(){
           public void done(ParseUser user, ParseException e){
             if (e == null){
              // save profile pic first
              user.put(KEY_PROFILE, photoFile);
              user.saveInBackground();
             } else {
               // Error
             }
           }
        });
        ```
      - (Update/PUT) Update username
       ```java
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser);
        query.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>(){
           public void done(ParseUser user, ParseException e){
             if (e == null){
              // save profile pic first
              user.setUsername("new_username");
              user.saveInBackground();
             } else {
               // Error
             }
           }
        });
        ```
        ```
#### [OPTIONAL:] Existing API Endpoints
##### Spotify Api
- Base URL - [https://api.spotify.com/v1](https://api.spotify.com/v1)

   HTTP Verb | Endpoint | Description
   ----------|----------|------------
    `GET`    | /search | get spotify catalog information about albums, artists, tracks that match a keyword string.
    `GET`    | /albums/{id} | Get Spotify catalog information for a single album by id.
    `GET`    | /tracks/{id}   | Get Spotify catalog information for a single track by id.
