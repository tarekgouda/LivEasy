package cse110.liveasy;

/*
This file uses source code from the tutorial
http://sourcey.com/beautiful-android-login-and-signup-screens-with-material-design/
for designing the login
This file uses the firebase authentication tutorial
https://firebase.google.com/docs/auth/android/start/
for implementing login up
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.Bind;

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


/**
 * Activity that lets a user log into their account using firebase authentication
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedPreferences;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();

    @Bind(R.id.input_username) EditText _usernameText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.link_signup) TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ButterKnife.bind(this);



        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        mAuth = FirebaseAuth.getInstance();

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

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
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

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        validateUser();

    }


    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);

        Intent intent =new Intent(LoginActivity.this,NavDrawerActivity.class);
        intent.putExtra("username", _usernameText.getText().toString());
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        Toast toast = Toast.makeText(LoginActivity.this, "Sign In Failed. Please try again.",
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();

        _loginButton.setEnabled(true);
    }
    /*
    Method sees that everything a correct username and password has been inputted. The method validates
    the user through its username and password, by seeing that the username exists and the email that
    is associated to that user, is associated to the correct password.
     */
    public void validateUser(){

        final String username = _usernameText.getText().toString();
        DatabaseReference uRef = ref.child("users");
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Signing In...");
        progressDialog.show();


        ValueEventListener listener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild("/"+ username + "/")){

                    Log.w(TAG, "signInWithUsername:failed", new Exception("Wrong Username"));

                    progressDialog.dismiss();

                    onLoginFailed();

                } else {


                    String email = (String) dataSnapshot.child(username).child("email").getValue();
                    String password = _passwordText.getText().toString();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", username);
                    editor.putString("email", email);
                    editor.putString("password", password);
                    editor.commit();

                    SharedPreferences sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Log.w(TAG, "signInWithEmail:failed", task.getException());

                                        progressDialog.dismiss();

                                        onLoginFailed();

                                    }
                                    else {
                                        new android.os.Handler().postDelayed(
                                                new Runnable() {
                                                    public void run() {
                                                        // On complete call either onLoginSuccess or onLoginFailed
                                                        onLoginSuccess();
                                                        // onLoginFailed();
                                                        progressDialog.dismiss();

                                                    }
                                                }, 3000);
                                    }

                                }
                            });

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        uRef.addListenerForSingleValueEvent(listener);
        uRef.removeEventListener(listener);

    }
    /*
    Method to see that all the information has been filled in
     */
    public boolean validate() {
        boolean valid = true;
        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty()) {
            _usernameText.setError("enter a username");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 8 || password.length() > 16) {
            _passwordText.setError("between 8 and 16 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
