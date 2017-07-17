package cse110.liveasy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void clickGoToProfile(View v) {
        //Toast.makeText(this, "Show some text on the screen.", Toast.LENGTH_LONG).show();
        Intent goToProfile = new Intent(this, ProfileActivity.class);
        startActivity(goToProfile);
        finish();
    }

    public void goToCreateGroup(View view){
        Intent goToCreateGroup = new Intent(this, CreateGroup.class);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String value = extras.getString("username");
            goToCreateGroup.putExtra("username", value);
        }

        startActivity(goToCreateGroup);
        finish();
    }

    public void goToJoinGroup(View view){

        Intent goToJoinGroup = new Intent(this, JoinGroup.class);

        startActivity(goToJoinGroup);
        finish();
    }
}
