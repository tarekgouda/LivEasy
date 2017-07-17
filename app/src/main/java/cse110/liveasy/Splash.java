package cse110.liveasy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Activity that introduces the application, and logs in a user if they have logged in before
 */
public class Splash extends AppCompatActivity {

    /** Duration of Splash **/
    private final int SPLASH_DURATION = 1500;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();

        String username = sharedpreferences.getString("username", "");
        // If the contents in sharedpreferences are not empty, then we log in using the user preferences
        if(!username.matches("") ) {
            // login with email and password method, after validating user has previously signed in
            validateUser();
        }
        else{
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent openWelcome = new Intent(Splash.this, LoginActivity.class);
                    Splash.this.startActivity(openWelcome);
                    Splash.this.finish();
                }
            }, SPLASH_DURATION);
        }

    }
    /*
    Method validates the user and logs them in upon start of the application if they had
    previously logged in but not logged out
     */
    public void validateUser(){

        DatabaseReference uRef = ref.child("users");

        ValueEventListener listener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final SharedPreferences sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();


                if(!dataSnapshot.hasChild("/"+ sharedpreferences.getString("username", "") + "/")){

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            Intent openWelcome = new Intent(Splash.this, LoginActivity.class);
                            Splash.this.startActivity(openWelcome);
                            Splash.this.finish();
                        }
                    }, SPLASH_DURATION);


                } else {

                    String email = sharedpreferences.getString("email","");
                    String password = sharedpreferences.getString("password", "");

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Splash.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {

                                        new Handler().postDelayed(new Runnable() {

                                            @Override
                                            public void run() {
                                                Intent openWelcome = new Intent(Splash.this, LoginActivity.class);
                                                Splash.this.startActivity(openWelcome);
                                                Splash.this.finish();
                                            }
                                        }, SPLASH_DURATION);

                                    }
                                    else {
                                        new android.os.Handler().postDelayed(
                                                new Runnable() {
                                                    public void run() {

                                                        Intent intent =new Intent(Splash.this,NavDrawerActivity.class);
                                                        intent.putExtra("username", sharedpreferences.getString("username", ""));
                                                        startActivity(intent);
                                                        finish();
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


}
