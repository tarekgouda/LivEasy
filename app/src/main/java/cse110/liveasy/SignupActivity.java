package cse110.liveasy;

/*
This file uses source code from the tutorial:
http://sourcey.com/beautiful-android-login-and-signup-screens-with-material-design/
for designing the signing up page
This file uses the firebase authentication tutorial
https://firebase.google.com/docs/auth/android/start/
for implementing sign up
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.Bind;

/**
 *Signup Activity is the class that controlls the user sign up logic, which uses firebase to authenticate
 *the user and create an account for the user
 */
public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    public FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();

    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedPreferences;

    @Bind(R.id.input_name) EditText _nameText;
    @Bind(R.id.input_username) EditText _usernameText;
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_mobile) EditText _mobileText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @Bind(R.id.btn_signup) Button _signupButton;
    @Bind(R.id.link_login) TextView _loginLink;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        // Listen when the user has been authenticated
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        // When the user clicks sign up
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        // When the user clicks the login link, when they already have an account
        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /*
    Signs up the user with an email and password account
     */
    public void signup() {
        Log.d(TAG, "Signup");

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");

        if (!validate()) {
            return;
        } else {

            progressDialog.show();

        }

        _signupButton.setEnabled(false);


        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();
        final String username = _usernameText.getText().toString();

        // Store the preferences on the device so user does not have to login every time
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("email", email);
        editor.putString("password", password);
        editor.commit();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference uref = database.getReference().child("users");

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean user_exists = dataSnapshot.hasChild(_usernameText.getText().toString());

                if(!user_exists) {

                    // [START create_user_with_email]
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        progressDialog.dismiss();

                                        onSignupFailed("Email is already associated with an account");
                                    }
                                    else{
                                        new android.os.Handler().postDelayed(
                                                new Runnable() {
                                                    public void run() {
                                                        // On complete call either onSignupSuccess or onSignupFailed
                                                        // depending on success
                                                        onSignupSuccess();

                                                        progressDialog.dismiss();
                                                    }
                                                }, 3000);
                                    }


                                }
                            });
                    // [END create_user_with_email]


                } else {

                    onSignupFailed("User name already exists. Please enter another.");
                    progressDialog.dismiss();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        uref.addListenerForSingleValueEvent(listener);
        uref.removeEventListener(listener);
    }

    /*
    When the user successfully signs up we create a new user in the database and move on to the
    questionaire
     */
    public void onSignupSuccess() {

        String username = _usernameText.getText().toString();
        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String mobile = _mobileText.getText().toString();


        DatabaseReference usersRef = ref.child("users");
        Map<String, Object> user_info = new HashMap<String, Object>();

        user_info.put("/"+username+"/", new User(name, mobile, email, false));
        usersRef.updateChildren(user_info);

        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(SignupActivity.this, Questionaire.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }

    /*
    When the user does not sign up correctly we let them try again
     */
    public void onSignupFailed(String message) {
        Toast toast = Toast.makeText(SignupActivity.this, message,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();

        _signupButton.setEnabled(true);
    }

    /*
    Method to validate that all user information is filled correctly
     */
    public boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (username.isEmpty() || username.length() < 3) {
            _usernameText.setError("at least 3 characters");
            onSignupFailed("User name must be least 3 characters");
            valid = false;
        }
        else if(username.contains(" ")){
            _usernameText.setError("username must not contain spaces");
            onSignupFailed("User name must not contain spaces");
            valid = false;
        }else {
            _usernameText.setError(null);
        }

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            onSignupFailed("Name must be at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            onSignupFailed("Please enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (mobile.isEmpty() || mobile.length()!=10) {
            _mobileText.setError("Enter Valid Mobile Number");
            onSignupFailed("Please enter a phone number in the format 0123456789");
            valid = false;
        } else {
            _mobileText.setError(null);
        }

        if (password.isEmpty() || password.length() < 8 || password.length() > 16) {
            _passwordText.setError("between 8 and 16 characters");
            onSignupFailed("Password must be between 8 and 16 characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 8 || reEnterPassword.length() > 16 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Passwords Do not match");
            onSignupFailed("Passwords must match");

            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }


}