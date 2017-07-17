package cse110.liveasy;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity to controls creating a group by a user
 */
public class CreateGroup extends AppCompatActivity {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
    }

    @Override
    public void onBackPressed(){

    }

    /*
    The key for the group becomes the key that is associated with the group in the firebase
    database
     */
    private String generateKey() {

        DatabaseReference groupsRef = ref.child("groups");

        return groupsRef.push().getKey();
    }

    /*
    Method that controlls creating a group, which is activated through the home page
     */
    public void createGroup(View view1) {

        EditText editText = (EditText) findViewById(R.id.editText5);
        final String groupName = editText.getText().toString();

        Bundle extras = getIntent().getExtras();
        final String username = extras.getString("username");
        DatabaseReference uRef = ref.child("users").child(username);
        final View view = view1;

        ValueEventListener listener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Boolean user_has_group = (Boolean) dataSnapshot.child("group").getValue();

                if (user_has_group) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateGroup.this);
                    builder.setTitle("\"" + groupName + "\"" + " cannot be created.");
                    builder.setMessage("You have already created a group. Cannot create more than one.");

                    builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent goBack = new Intent(CreateGroup.this, NavDrawerActivity.class);
                            goBack.putExtra("username", username);
                            startActivity(goBack);
                            finish();
                        }
                    });
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            Intent goBack = new Intent(CreateGroup.this, NavDrawerActivity.class);
                            goBack.putExtra("username", username);
                            startActivity(goBack);
                            finish();
                        }
                    });

                    builder.create().show();

                } else {
                    if (!groupName.matches("") && !groupName.matches("Group Name")) {
                        final String groupKey = generateKey();
                        //CHECK TO SEE THAT KEY DOES NOT EXIST
                        DatabaseReference groupsRef = ref.child("groups");
                        Map<String, Object> group_info = new HashMap<String, Object>();
                        Map<String, Object> members = new HashMap<String, Object>();


                        // Set user's group boolean to true
                        DatabaseReference usersRef = ref.child("users").child(username);
                        Map<String, Object> group_bool = new HashMap<String, Object>();
                        group_bool.put("/group/", new Boolean(true));
                        usersRef.updateChildren(group_bool);

                        Map<String, Object> groupID = new HashMap<String, Object>();
                        groupID.put("/groupID/", new String(groupKey));
                        usersRef.updateChildren(groupID);

                        Map<String, Object> user_content = (Map<String,Object>) dataSnapshot.getValue();
                        user_content.put("groupID", groupKey);
                        user_content.put("group", new Boolean(true));
                        members.put(username, user_content );
                        group_info.put("/" + groupKey + "/", new Group(groupName, members, 1));
                        groupsRef.updateChildren(group_info);

                        ArrayList<String> pending = new ArrayList<String>();
                        Map<String, Object> pending_folder = new HashMap<String, Object>();
                        pending.add("");
                        pending_folder.put("/pending/", pending);
                        groupsRef.child(groupKey).updateChildren(pending_folder);


                        /*********************************************/
                        // to be used for removing users from group
                        ArrayList<String> usersToBeDeleted = new ArrayList<String>();
                        Map<String, Object> usersToBeDeleted_folder = new HashMap<String, Object>();
                        Map<String, Object> usersToBeDeleted_default = new HashMap<String, Object>();
                        usersToBeDeleted.add("");
                        usersToBeDeleted_default.put("default", usersToBeDeleted);
                        usersToBeDeleted_folder.put("/usersToBeDeleted/", usersToBeDeleted_default);
                        groupsRef.child(groupKey).updateChildren(usersToBeDeleted_folder);
                        /**********************************************/


                        Map<String, Object> address = new HashMap<>();
                        address.put("address", "Please enter your address");
                        groupsRef.child(groupKey).updateChildren(address);

                        Map<String, Object> group_photo = new HashMap<>();
                        group_photo.put("group_photo", "https://firebasestorage.googleapis.com/v0/b/liveasy-85049.appspot.com/o/addhouse.jpg?alt=media&token=ad5a0da8-73ae-4a0d-8272-4572f93ec33b");
                        groupsRef.child(groupKey).updateChildren(group_photo);
                        //Add group chat child onto database
                        Map<String, Object> chatMap = new HashMap<String, Object>();
                        chatMap.put("chat_room", "");
                        groupsRef.child(groupKey).updateChildren(chatMap);

                        ArrayList<String> tasks = new ArrayList<String>();
                        tasks.add("");
                        Map<String, Object> tasks_folder = new HashMap<String, Object>();
                        Map<String, Object > user_tasks = new HashMap<String, Object>();
                        user_tasks.put(username, tasks);
                        tasks_folder.put("tasks", user_tasks);
                        groupsRef.child(groupKey).updateChildren(tasks_folder);


                        Context context = view.getContext();
                        LinearLayout layout = new LinearLayout(context);
                        layout.setOrientation(LinearLayout.VERTICAL);

                        TextView title = new TextView(view.getContext());
                        title.setText("\n\"" + groupName + "\"");
                        title.setTextSize(30);
                        title.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

                        TextView message1 = new TextView(view.getContext());
                        message1.setText("created, this is your group's id:");
                        message1.setTextSize(20);
                        message1.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

                        TextView groupKeyTextView = new TextView(view.getContext());
                        groupKeyTextView.setText(groupKey);
                        groupKeyTextView.setTextSize(40);
                        groupKeyTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

                        TextView message2 = new TextView(view.getContext());
                        message2.setText("Please share this key with your roommates so that they may join.");
                        message2.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                        layout.addView(title);
                        layout.addView(message1);
                        layout.addView(groupKeyTextView);
                        layout.addView(message2);

                        AlertDialog.Builder builder = new AlertDialog.Builder(CreateGroup.this);
                        builder.setView(layout);
                        builder.setNeutralButton("Copy key to clipboard", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("Copied", groupKey);
                                clipboard.setPrimaryClip(clip);
                            }
                        });
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent goBack = new Intent(CreateGroup.this, NavDrawerActivity.class);
                                goBack.putExtra("username", username);
                                startActivity(goBack);
                                finish();
                            }
                        });

                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                Intent goBack = new Intent(CreateGroup.this, NavDrawerActivity.class);
                                goBack.putExtra("username", username);
                                startActivity(goBack);
                                finish();
                            }
                        });

                        builder.create().show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(CreateGroup.this);
                        builder.setMessage("Please type in a group name");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Do nothing
                            }
                        });
                        builder.create().show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        uRef.addListenerForSingleValueEvent(listener);
        uRef.removeEventListener(listener);

    }


    public void cancelCreateGroup(View view){
        Intent goBack = new Intent(this, NavDrawerActivity.class);
        Bundle extras = getIntent().getExtras();
        goBack.putExtra("username",  extras.getString("username") );
        startActivity(goBack);
        finish();
    }
}
