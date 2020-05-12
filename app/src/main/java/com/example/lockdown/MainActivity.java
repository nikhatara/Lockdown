package com.example.lockdown;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;

//views

    Button registerButton, loginButton;
    SignInButton mGoogleLoginBtn;
    GoogleSignInClient mGoogleSignInClient;
    //Declare an instance of Firebase Auth.
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//init views

        registerButton = findViewById(R.id.register);
        loginButton = findViewById(R.id.login);
// in the onCreate method, Initialize the FirebaseAuth object.
        mAuth = FirebaseAuth.getInstance();
// Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        mGoogleLoginBtn = findViewById(R.id.gSignInBtn);
        mGoogleLoginBtn.setSize(SignInButton.SIZE_STANDARD);

//handle register button click
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//start new activity
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));

            }
        });
//handle login button click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//start new activity
                startActivity(new Intent(MainActivity.this,LoginActivity.class));

            }
        });
//handle Google sign in button click
        mGoogleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);

            }
        });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
// Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
// Google Sign In failed, update UI appropriately

                Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
// ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
// Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();

//if user is signing in for the first time, then get and show the user's info from google account


//show user email in toast
                            Toast.makeText(MainActivity.this,""+user.getEmail(),Toast.LENGTH_SHORT).show();
//go to profile activity after logged in
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                            finish();
//updateUI(user);
                        } else {
// If sign in fails, display a message to the user.

                            Toast.makeText(MainActivity.this,"SignIn Failed !",Toast.LENGTH_SHORT).show();
//updateUI(null);
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//get and show error message

                Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        })
        ;
    }

}