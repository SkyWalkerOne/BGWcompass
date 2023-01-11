package com.example.bgwcompass;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    GoogleSignInOptions gsio;
    GoogleSignInClient gsic;
    GoogleSignInClient googleSignInClient;

    private CallbackManager mCallbackManager;
    LoginButton onFaceBookEntry;

    private FirebaseAuth mAuth;
    static FirebaseUser user;

    static ArrayList<userDescription> foundUsers;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        mCallbackManager = CallbackManager.Factory.create();
        onFaceBookEntry = findViewById(R.id.verify0);
        mAuth = FirebaseAuth.getInstance();
        foundUsers = new ArrayList<>();

        onFaceBookEntry.setReadPermissions("email", "public_profile");
        onFaceBookEntry.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {}

            @Override
            public void onError(FacebookException error) {}
        });

        gsio = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("572093130311-r00iir0em6mmd2f3jiv7ds8okdte1nks.apps.googleusercontent.com")
                .requestEmail()
                .build();
        gsic = GoogleSignIn.getClient(this, gsio);
        googleSignInClient= GoogleSignIn.getClient(MainActivity.this, gsio);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();

        hideSystemBars();

        if(mAuth.getCurrentUser() != null)
            goNext();
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null)
            Toast.makeText(MainActivity.this, "Currently Logged in: " + currentUser.getEmail(), Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==100)
        {
            Task<GoogleSignInAccount> signInAccountTask=GoogleSignIn.getSignedInAccountFromIntent(data);
            {
                try {
                    GoogleSignInAccount googleSignInAccount=signInAccountTask.getResult(ApiException.class);
                    if(googleSignInAccount!=null)
                    {
                        AuthCredential authCredential= GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(),null);
                        mAuth.signInWithCredential(authCredential)
                                .addOnCompleteListener(this, task -> {
                                    if(task.isSuccessful()) {
                                        goNext();
                                    } else
                                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                });

                    }
                }
                catch (ApiException e) { e.printStackTrace(); }
            }
        } else {
            try {
                mCallbackManager.onActivityResult(requestCode, resultCode, data);
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        goNext();
                        Toast.makeText(MainActivity.this, "Authentication Succeeded.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        Log.i("###111", task.getResult().toString());
                    }
                });
    }

    public void onGooglePress(View view) {
        Intent intent = gsic.getSignInIntent();
        startActivityForResult(intent, 100);
    }

    public void goNext() {
        user = mAuth.getCurrentUser();
        finish();
        startActivity(new Intent(MainActivity.this, register.class));
    }

    private void hideSystemBars() {
        WindowInsetsControllerCompat windowInsetsController =
                ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsController == null) {
            return;
        }
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
    }
}