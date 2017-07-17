package cse110.liveasy;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Acitivity to let a user manage the requests sent to join their group, where they could either
 * accept or reject users.
 */
public class ManageRequests extends AppCompatActivity {
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_requests);

        //place a back button on the top right of the screen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        extras = getIntent().getExtras();

        //get pending list passed in through activty
        listItems = extras.getStringArrayList("pending");

        //linear layout to hold pending requests
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(this);
        title.setText("Pending Requests");
        title.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        title.setTextSize(18);
        title.setPadding(0,15,0,100);
        title.setTypeface(title.getTypeface(), Typeface.BOLD);

        layout.addView(title);

        //iterate through pending requests
        for( int index = 1; index < listItems.size(); index++ ) {

            final LinearLayout inner_layout = new LinearLayout(this);
            inner_layout.setBackgroundResource(R.drawable.in_message);

            //text view for pending user
            TextView user = new TextView(this);
            user.setText(listItems.get(index));
            user.setTextSize(24);

            user.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);

            LinearLayout.LayoutParams button_param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f);

            button_param.setMargins(0,10,5,10);

            LinearLayout.LayoutParams user_param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);

            user.setLayoutParams(user_param);

            final String currentUsername = listItems.get(index);

            // Button to accept a user
            Button accept = new Button(this);
            accept.setText("Accept");
            accept.setBackgroundResource(R.drawable.btn_accept_background);
            accept.setLayoutParams(button_param);
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //find user
                    final FirebaseDatabase ref = FirebaseDatabase.getInstance();
                    final DatabaseReference uRef = ref.getReference().child("users").child(currentUsername);

                    ValueEventListener userListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final Map<String, Object> currentUserMap = (HashMap<String, Object>) dataSnapshot.getValue();

                            if((boolean)currentUserMap.get("isPending")){
                                final Map<String, Object> updateUser = new HashMap<String, Object>();
                                //set group to true
                                updateUser.put("/group/", new Boolean(true));
                                //set groupID to this group
                                updateUser.put("/groupID/", extras.getString("groupKey"));
                                //set pending to false
                                updateUser.put("/isPending/", new Boolean(false));
                                //uRef.updateChildren(updateUser);

                                final DatabaseReference gRef = ref.getReference().child("groups").child((String) extras.getString("groupKey"));
                                ValueEventListener groupListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot1) {
                                        Map<String, Object> group = (HashMap<String, Object>) dataSnapshot1.getValue();

                                        Map<String, Object> members = (HashMap<String, Object>) group.get("members");
                                        members.put(currentUsername, currentUserMap);
                                        group.put("members", members);

                                        int currentCount = ((Long) (group.get("num_users"))).intValue();
                                        currentCount++;
                                        group.put("num_users", currentCount);

                                        ArrayList<String> pending = (ArrayList<String>) dataSnapshot1.child("pending").getValue();
                                        pending.remove(currentUsername);
                                        group.put("pending", pending);


                                        ArrayList<String> tasks = new ArrayList<String>();
                                        tasks.add("");
                                        Map<String,Object> user_tasks = (HashMap)group.get("tasks");
                                        user_tasks.put(currentUsername, tasks);
                                        group.put("tasks", user_tasks);

                                        gRef.updateChildren(group);

                                        uRef.updateChildren(updateUser);


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                };
                                gRef.addListenerForSingleValueEvent(groupListener);
                                gRef.removeEventListener(groupListener);

                                Toast toast = Toast.makeText(ManageRequests.this, currentUsername+" has been accepted",
                                        Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER,0,0);
                                toast.show();
                            }
                            else{
                                Toast toast = Toast.makeText(ManageRequests.this, currentUsername + " has previously been accepted or rejected",
                                        Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER,0,0);
                                toast.show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    uRef.addListenerForSingleValueEvent(userListener);
                    uRef.removeEventListener(userListener);

                    inner_layout.setVisibility(LinearLayout.GONE);
                }
            });

            // Button to rekect a user
            Button reject =  new Button(this);
            reject.setText("Reject");
            reject.setBackgroundResource(R.drawable.btn_reject_background);
            reject.setLayoutParams(button_param);
            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //find user
                    final FirebaseDatabase ref = FirebaseDatabase.getInstance();
                    final DatabaseReference uRef = ref.getReference().child("users").child(currentUsername);

                    ValueEventListener userListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final Map<String, Object> currentUserMap = (HashMap<String, Object>) dataSnapshot.getValue();

                            if((boolean)currentUserMap.get("isPending")){
                                Map<String, Object> updateUser = new HashMap<String, Object>();
                                updateUser.put("/isPending/", new Boolean(false));
                                uRef.updateChildren(updateUser);

                                final DatabaseReference gRef = ref.getReference().child("groups").child((String) extras.getString("groupKey"));
                                ValueEventListener groupListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot1) {
                                        Map<String, Object> group = (HashMap<String, Object>) dataSnapshot1.getValue();

                                        ArrayList<String> pending = (ArrayList<String>) dataSnapshot1.child("pending").getValue();
                                        pending.remove(currentUsername);
                                        group.put("pending", pending);
                                        gRef.updateChildren(group);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                };
                                gRef.addListenerForSingleValueEvent(groupListener);
                                gRef.removeEventListener(groupListener);

                                Toast toast = Toast.makeText(ManageRequests.this, currentUsername+" has been rejected",
                                        Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER,0,0);
                                toast.show();
                            }
                            else{
                                Toast toast = Toast.makeText(ManageRequests.this, currentUsername+" has previously been accepted or rejected",
                                        Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER,0,0);
                                toast.show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    uRef.addListenerForSingleValueEvent(userListener);
                    uRef.removeEventListener(userListener);

                    inner_layout.setVisibility(LinearLayout.GONE);
                }
            });


            inner_layout.addView(user, LinearLayout.LayoutParams.WRAP_CONTENT);
            inner_layout.addView(accept);
            inner_layout.addView(reject);

            layout.addView(inner_layout);

        }


        setContentView(layout);
    }


    /**
     * Switch case for options selected (for pressing the back button on the top right)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent goBack = new Intent(this, NavDrawerActivity.class);
                goBack.putExtra("username", (String)extras.get("username"));
                startActivity(goBack);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Override this method so the activity does nothing when back button is pressed
     */
    @Override
    public void onBackPressed(){

    }

}
