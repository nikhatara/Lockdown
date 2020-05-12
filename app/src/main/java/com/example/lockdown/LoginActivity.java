package com.example.lockdown;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    //views
    EditText emailID, passWord;
    TextView dontHaveAcc, forgotPassword;
    Button loginBtn;


    //Declare an instance of Firebase Auth.
    private FirebaseAuth mAuth;

    //progress dialog
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//Actionbar and its Title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Login");
//Enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

//Before mAuth


//init
        emailID = findViewById(R.id.emailText);
        passWord = findViewById(R.id.passwordText);
        dontHaveAcc = findViewById(R.id.newAccountText);
        forgotPassword = findViewById(R.id.forgotPasswordText);
        loginBtn = findViewById(R.id.loginBtn);

// in the onCreate method, Initialize the FirebaseAuth object.
        mAuth = FirebaseAuth.getInstance();

//set login button click
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailID.getText().toString().trim();
                String password = passWord.getText().toString().trim();

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//invalid email pattern, set error
                    emailID.setError("Invalid Email");
                    emailID.setFocusable(true);
                } else loginUser(email, password);

            }
        });
// "Don't have an acoount? Register" Text view click
        dontHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
// Forgot Password ? Recover Password
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });


//init Progress Dialog
        pd = new ProgressDialog(this);


    }

    private void showRecoverPasswordDialog() {
//Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

//set Linear Layout
        LinearLayout linearLayout = new LinearLayout(this);

//views to set in layout
        final EditText accountEmail = new EditText(this);
        accountEmail.setHint("Email");
        accountEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
// set the minimum width of a text view to fit a text of n 'M' letters regardless o
// of the actual text extension and text size
        accountEmail.setMinEms(16);

        linearLayout.addView(accountEmail);
        linearLayout.setPadding(10, 10, 10, 10);

        builder.setView(linearLayout);
//recover button
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//input email
                String email = accountEmail.getText().toString().trim();
//begin recovery
                beginRecovery(email);

            }
        });
//cancel dialog button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
// cancel/dismiss password recovery Dialog
                dialog.dismiss();
            }
        });
//show Dialog
        builder.create().show();
    }

    private void beginRecovery(String email) {
//Show progress Dialog
        pd.setMessage("Sending email");
        pd.show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Failed...", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
//get and show proper error message
                Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser(String email, String password) {
//Show progress Dialog
        pd.setMessage("Logging In");
        pd.show();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
//dismiss progress dialog
                    pd.dismiss();
//Sign in success, update UI with the signed in user's information
                    FirebaseUser user = mAuth.getCurrentUser();
//user is logged in, So start Profile Activity
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } else {
//dismiss progress dialog
                    pd.dismiss();
//if sign in fails, display a message to the user.
                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//dismiss progress dialog
                pd.dismiss();
//get the error and show error message
                Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}