package com.example.lockdown;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    //views
    EditText emailID, passWord;
    Button registerBtn;
    TextView haveAccAlready;
    //progressbar to display while registering users
    ProgressDialog progressDialog;

    //declare an instance of Firebase auth
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account");

        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);

//init
        emailID = findViewById(R.id.email);
        passWord = findViewById(R.id.password);
        registerBtn = findViewById(R.id.registerButton);
        haveAccAlready = findViewById(R.id.existingAccountText);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User....");

//in the oncreate method, initialize the FirebaseAuth object.
        mAuth = FirebaseAuth.getInstance();

//handling register button click
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailID.getText().toString().trim();
                String password = passWord.getText().toString().trim();
//validate
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)  ){
                    Toast.makeText(RegisterActivity.this, "Required fields need to be filled with correct inputs",
                            Toast.LENGTH_SHORT).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
//fix error and set focus to email field
                    emailID.setError("Invalid Email");
                    emailID.setFocusable(true);
                } else if (password.length()<6){
//fix error and set focus to password field
                    passWord.setError("Minimum 6 Characters for Password");
                    passWord.setFocusable(true);
                } else {
//register the user
                    registerUser(email,password);
                }
            }
        });

//handle "have account already? Log In" text view, click listener
        haveAccAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
            }
        });

    }

    private void registerUser(String email, String password) {

//email and password is valid, show process dialog and start registering users
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
// Sign in success, dismiss dialog and start Register Activity
                            progressDialog.dismiss();
                            Log.d("tag","createUserWithEmail:s");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(RegisterActivity.this,"Registered\n"+user.getEmail(),Toast.LENGTH_SHORT);
                            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                            finish();
                        }
                        else {
// If sign in fails, dismiss dialog and display a message to the user.
                            progressDialog.dismiss();
                            Log.w("tag","createUserWithEmail:f", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//dismiss process dialog and get and show the error message
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, ""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });


    }


    @Override
    public boolean onSupportNavigateUp() {
//go previous screen
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
