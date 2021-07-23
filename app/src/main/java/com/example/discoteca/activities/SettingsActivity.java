package com.example.discoteca.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.discoteca.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SettingsActivity extends AppCompatActivity {

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PROFILE = "profile";
    public final static int PICK_PHOTO_CODE = 27;
    private ParseFile photoFile;
    private String TAG = "SettingsActivity";
    ImageView ivUserPict;
    EditText etNameEdit;
    TextView tvEmail;
    Button btnSaveProf;
    ImageButton ibClose;
    FloatingActionButton fabEditImage;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // Gets current user data
        ParseUser user = ParseUser.getCurrentUser();

        context = this;

        // Get references
        ivUserPict = findViewById(R.id.ivUserPict);
        etNameEdit = findViewById(R.id.etNameEdit);
        tvEmail = findViewById(R.id.tvEmail);
        btnSaveProf = findViewById(R.id.btnSaveProf);
        ibClose = findViewById(R.id.ibClose);
        fabEditImage = findViewById(R.id.fabEditImage);

        // Bind data
        etNameEdit.setText(user.getUsername());
        tvEmail.setText(user.getEmail());
        Glide.with(context).load(user.getParseFile(KEY_PROFILE).getUrl()).circleCrop().into(ivUserPict);

        btnSaveProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser currUser = ParseUser.getCurrentUser();
                String newUsername = etNameEdit.getText().toString();
                saveUser(newUsername, currUser);

            }
        });

        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fabEditImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPhoto(v);
            }
        });

    }

    private void onClickPhoto(View v) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();
            String photoPath = photoUri.getPath();

            // Load the image located at photoUri into selectedImage
            Bitmap selectedImage = loadFromUri(photoUri);


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            photoFile = new ParseFile(byteArray);

            // Load the selected image into a preview
            ivUserPict.setImageBitmap(selectedImage);
        }
    }

    private void saveUser(String newUsername, ParseUser currentUser) {
        currentUser.put(KEY_USERNAME, newUsername);
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error while saving description", e);
                    Toast.makeText(SettingsActivity.this, "Error while saving!", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(SettingsActivity.this, "Your username was save successfully", Toast.LENGTH_SHORT).show();
            }
        });

    }
}