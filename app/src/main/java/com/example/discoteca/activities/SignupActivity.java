package com.example.discoteca.activities;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.discoteca.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SignupActivity extends AppCompatActivity {

    private String TAG = "SignupActivity";
    public static final String KEY_PROFILE = "profile";
    public final static int PICK_PHOTO_CODE = 27;
    private ParseFile photoFile;
    Button btnNewus;
    EditText etUsersign;
    EditText etPassign;
    EditText etEmail;
    ImageView ivSignPicture;
    FloatingActionButton fabImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        btnNewus = findViewById(R.id.btnNewus);
        etUsersign = findViewById(R.id.etUsersign);
        etPassign = findViewById(R.id.etPassign);
        etEmail = findViewById(R.id.etEmail);
        ivSignPicture = findViewById(R.id.ivSignPicture);
        fabImage = findViewById(R.id.fabImage);

        btnNewus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsersign.getText().toString();
                String password = etPassign.getText().toString();
                String email = etEmail.getText().toString();
                if (username.isEmpty() || password.isEmpty() || email.isEmpty()){
                    Toast.makeText(SignupActivity.this, "Make sure not to leave anything blank!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Create the ParseUser
                ParseUser user = new ParseUser();

                // Set properties
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Toast.makeText(SignupActivity.this, "Failed to signup, try again!", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Failed to signup", e);
                            return;
                        }
                        Toast.makeText(SignupActivity.this, "Signup successfully!", Toast.LENGTH_LONG).show();
                        Intent result = new Intent();
                        result.putExtra("username", username);
                        result.putExtra("password", password);
                        setResult(RESULT_OK, result);
                        finish();
                    }
                });
            }

        });

        fabImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPhoto(v);
            }
        });
    }

    // Trigger gallery selection for a photo
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
            ivSignPicture.setImageBitmap(selectedImage);
        }
    }

}