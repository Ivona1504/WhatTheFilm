package fer.ppij.whatthefilm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import fer.ppij.whatthefilm.model.User;

public class CreateAccountActivity extends AppCompatActivity {
    private Button mCreateUserButton;
    private EditText mUsernameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private TextView mLoginTextView;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ProgressDialog mAuthProgressDialog;

    private String mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mCreateUserButton = (Button) findViewById(R.id.createUserButton);
        mUsernameEditText = (EditText) findViewById(R.id.usernameEditText);
        mEmailEditText = (EditText) findViewById(R.id.emailEditText);
        mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);
        mConfirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);
        mLoginTextView = (TextView) findViewById(R.id.loginTextView);

        mAuth = FirebaseAuth.getInstance();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");

        mLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        mCreateUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewUser();
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };

        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading...");
        mAuthProgressDialog.setMessage("Authenticating with Firebase...");
        mAuthProgressDialog.setCancelable(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void createNewUser() {
        mName = mUsernameEditText.getText().toString().trim();
        final String email = mEmailEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();
        String confirmPassword = mConfirmPasswordEditText.getText().toString().trim();

        boolean validEmail = isValidEmail(email);
        boolean validUsername = isValidUsername(mName);
        boolean validPassword = isValidPassword(password, confirmPassword);
        if (!validEmail || !validUsername || !validPassword) return;

        mAuthProgressDialog.show();

        Log.i("FIREBASE", "ovjde");

        Toast.makeText(CreateAccountActivity.this, "Username: " + email, Toast.LENGTH_SHORT).show();
        Toast.makeText(CreateAccountActivity.this, "Password: " + password, Toast.LENGTH_SHORT).show();


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        mAuthProgressDialog.dismiss();

                        if (task.isSuccessful()) {
                            createFirebaseUserProfile(task.getResult().getUser());
                            addUserToDatabase(mName);
                            Toast.makeText(CreateAccountActivity.this, "You have successfully registered to WhatTheFilm app!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CreateAccountActivity.this, "Failed!" + task.getResult(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean isValidEmail(String email) {
        boolean isGood = (email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGood) {
            mEmailEditText.setError("Please enter a valid email address");
            return false;
        }
        return isGood;
    }

    private boolean isValidUsername(String name) {
        if (name.equals("")) {
            mUsernameEditText.setError("Please enter username");
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String password, String confirm) {
        if (password.length() < 6) {
            mPasswordEditText.setError("Please create a password at least 6 characters long");
            return false;
        } else if (!password.equals(confirm)) {
            mPasswordEditText.setError("Passwords do not match");
            return false;
        }
        return true;
    }

    private void addUserToDatabase(String name) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        User user = new User(firebaseUser.getUid(), firebaseUser.getEmail(), name);
        mUsersDatabaseReference.child(firebaseUser.getUid()).setValue(user);
        //mUsersDatabaseReference.child(firebaseUser.getUid()).child("friends").child("prvi").setValue(firebaseUser.getDisplayName());
    }

    private void createFirebaseUserProfile(final FirebaseUser user) {
        Toast.makeText(CreateAccountActivity.this, "createFirebaseUserProfile", Toast.LENGTH_SHORT).show();
        UserProfileChangeRequest addProfileName = new UserProfileChangeRequest.Builder()
                .setDisplayName(mName)
                .build();
        Toast.makeText(CreateAccountActivity.this, "Second", Toast.LENGTH_SHORT).show();
        user.updateProfile(addProfileName).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("CREATE_ACTIVITY_ACCOUNT", user.getDisplayName());
                }
            }
        });
    }
}
