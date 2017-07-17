package cse110.liveasy;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
/**
 * Activity that lets the user vote for removing other users
 */
public class RemoveUserFromGroup extends AppCompatActivity {

    Bundle extras;
    String username;
    DatabaseReference gref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_user_from_group);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final ArrayList<String> listItems = new ArrayList<String>();
        extras = getIntent().getExtras();
        username = extras.getString("username");
        final String groupKey = extras.getString("groupID");

        final LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(this);
        title.setText("Group Members List");
        title.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        title.setTextSize(18);
        title.setPadding(0, 15, 0, 100);
        title.setTypeface(title.getTypeface(), Typeface.BOLD);

        layout.addView(title);
        gref = FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey);

        ValueEventListener readMembers = new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                Map<String,Object> memberContents = (Map<String, Object>)dataSnapshot.child("members").getValue();
                Set<String> memberList = memberContents.keySet();

                Iterator iterator = memberList.iterator();

                // check value
                while (iterator.hasNext()){

                    String currentUser = (String)iterator.next();
                    if( currentUser.compareTo(username) != 0 ) {
                        listItems.add(currentUser);

                    }
                }

                for (int index = 0; index < listItems.size(); index++) {

                    final LinearLayout inner_layout = new LinearLayout(RemoveUserFromGroup.this);
                    inner_layout.setBackgroundResource(R.drawable.in_message);

                    TextView user = new TextView(RemoveUserFromGroup.this);
                    user.setText(listItems.get(index));
                    user.setTextSize(24);

                    user.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);

                    LinearLayout.LayoutParams button_param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f);
                    button_param.setMargins(0,10,0,10);

                    LinearLayout.LayoutParams user_param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);

                    user.setLayoutParams(user_param);



                    final String currentUsername = listItems.get(index);

                    Button removeUser = new Button(RemoveUserFromGroup.this);
                    removeUser.setText("Remove");
                    removeUser.setBackgroundResource(R.drawable.btn_reject_background);
                    removeUser.setLayoutParams(button_param);
                    removeUser.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Map<String, Object> usersToBeDeletedMap = (Map<String, Object>)
                                    dataSnapshot.child("usersToBeDeleted").getValue();

                            Long number_users_group = (Long)dataSnapshot.child("num_users").getValue();

                            // has the user been flagged to be removed?
                            if( dataSnapshot.child("usersToBeDeleted").hasChild(currentUsername) ) {

                                // retrieve a list of group members that voted this user (currentUsername) out
                                ArrayList<String> member_votes_list = (ArrayList<String>)dataSnapshot
                                        .child("usersToBeDeleted")
                                        .child(currentUsername)
                                        .getValue();

                                // have you voted this user out yet?
                                if( member_votes_list.contains(username) ) {
                                    Toast toast = Toast.makeText(RemoveUserFromGroup.this,
                                            "You have already voted "+currentUsername+
                                                " out of the group.\nYou may not vote more than once.",
                                            Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER,0,0);
                                    toast.show();

                                } else {
                                    // you vote this user out, and check if your vote is now majority
                                    member_votes_list.add(username);

                                    // did your vote make it majority votes?
                                    if( member_votes_list.size() >= Math.floor( number_users_group.doubleValue() / 2 ) ) {

                                        removeUserFromGroup(currentUsername, usersToBeDeletedMap,
                                                (Map<String, Object>)dataSnapshot.getValue(), number_users_group.intValue() );

                                    } else {

                                        // add yourself to the list to count your vote
                                        Map<String, Object> member_to_be_deleted = new HashMap<String, Object>();
                                        member_to_be_deleted.put(currentUsername,member_votes_list);
                                        gref.child("usersToBeDeleted").child(currentUsername)
                                                .updateChildren(member_to_be_deleted);

                                        Toast toast = Toast.makeText(RemoveUserFromGroup.this,
                                                "You have voted "+currentUsername+
                                                        " out of the group.",
                                                Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER,0,0);
                                        toast.show();
                                    }
                                }

                            } else {

                                // add them to the users to be deleted list and set that you voted them out
                                // and check if your vote is now majority

                                // your vote was majority
                                if( 1 >= Math.floor( number_users_group.doubleValue() / 2 )  ) {

                                    removeUserFromGroup(currentUsername, usersToBeDeletedMap,
                                            (Map<String, Object>)dataSnapshot.getValue(), number_users_group.intValue() );

                                } else {
                                    // add them to the usersToBeDeleted list
                                    ArrayList<String> members_votes_list =  new ArrayList<String>();
                                    members_votes_list.add(username);

                                    Map<String, Object> userToBeRemoved = new HashMap<String, Object>();
                                    userToBeRemoved.put(currentUsername, members_votes_list);

                                    gref.child("usersToBeDeleted").updateChildren(userToBeRemoved);

                                    Toast toast = Toast.makeText(RemoveUserFromGroup.this,
                                            "You have requested and voted "+currentUsername+
                                                    " out of the group.",
                                            Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER,0,0);
                                    toast.show();
                                }
                            }


                            inner_layout.setVisibility(LinearLayout.GONE);
                        }
                    });


                    inner_layout.addView(user, LinearLayout.LayoutParams.WRAP_CONTENT);
                    inner_layout.addView(removeUser);

                    layout.addView(inner_layout);
                }

                setContentView(layout);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        gref.addListenerForSingleValueEvent(readMembers);

    }

    /*
    Method that removes a user from the group called upon when voting user makes the majority
     */
    public void removeUserFromGroup(String user_to_be_removed, Map<String, Object> usersToBeDeletedMap
        , Map<String, Object> groupMap, int num_users) {

        Map<String, Object> membersMap = (Map<String, Object>)groupMap.get("members");

        // remove user from usersToBeDeleted List
        usersToBeDeletedMap.remove(user_to_be_removed);
        gref.child("usersToBeDeleted").updateChildren(usersToBeDeletedMap);

        //remove users tasks
        Map<String,Object> tasks = (HashMap)groupMap.get("tasks");
        tasks.remove(user_to_be_removed);
        groupMap.put("tasks", tasks);


        // remove contents of user_to_be_removed from group
        membersMap.remove(user_to_be_removed);
        groupMap.put("members", membersMap);
        gref.updateChildren(groupMap);

        // update number of users in the group
        gref.child("num_users").setValue(new Integer(num_users-1));

        // flag the removed user's account as having no group
        FirebaseDatabase ref = FirebaseDatabase.getInstance();
        DatabaseReference uref = ref.getReference().child("users").child(user_to_be_removed);
        uref.child("group").setValue(new Boolean(false));
        uref.child("groupID").setValue(new String(""));

        // Notify current logged in user that their vote has removed user from group
        Toast toast = Toast.makeText(RemoveUserFromGroup.this,
                "Your vote has removed "+user_to_be_removed+
                        " out of the group.",
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();

        // restart activity
        Intent restart = new Intent(this, RemoveUserFromGroup.class);
        restart.putExtra("username", (String)extras.get("username"));
        restart.putExtra("groupID", extras.getString("groupID"));
        startActivity(restart);
        finish();

    }

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

    @Override
    public void onBackPressed(){

    }
}
