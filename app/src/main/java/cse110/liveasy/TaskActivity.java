package cse110.liveasy;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
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

/**
 * Activity that controls the logic for adding tasks for yourself and other users
 */
public class TaskActivity extends AppCompatActivity {

    public Bundle extras;
    public String groupID;
    public String user;
    public Map<String, Object> members;
    public ArrayList<String> userTasks = new ArrayList<>();
    public LinearLayout layout;
    public DatabaseReference ref;
    public ValueEventListener refreshListener;
    public Boolean hasRefreshButton = false;
    public Boolean needsRefreshButton = false;
    public Boolean refreshButtonAdded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        //get initial info
        this.extras = getIntent().getExtras();
        this.groupID = extras.getString("group_id");
        this.members = (HashMap<String, Object>)extras.get("members");
        this.user = extras.getString("username");

        //only run if user has a group
        if(!this.groupID.equals("") && !this.groupID.equals("requested")) {
            ref = FirebaseDatabase.getInstance().getReference().child("groups").child(groupID).child("tasks");

            //set back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            layout = (LinearLayout) findViewById(R.id.task_layout);

            //"Tasks" as title
            TextView title = new TextView(this);
            title.setText("Tasks");
            title.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            title.setTextSize(18);
            title.setPadding(0, 15, 0, 0);
            title.setTypeface(title.getTypeface(), Typeface.BOLD);

            layout.addView(title);

            //fill page with user tasks
            populateLayout(layout);
            startRefreshListener();
        }
        //display a message when user doesn't have a group
        else{
            layout = (LinearLayout) findViewById(R.id.task_layout);

            //set back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            TextView noGroupText = new TextView(this);
            noGroupText.setText("You are not in a group yet.");
            noGroupText.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            noGroupText.setPadding(0, 200, 0, 0);
            noGroupText.setTextSize(28);
            layout.addView(noGroupText);
        }
    }


    /**
     * Add all of the tasks in the database from the respective members (in the bundle)
     * @param l master layout
     */
    public void populateLayout(LinearLayout l){
        //create tasks reference
        DatabaseReference tRef = FirebaseDatabase.getInstance().getReference().child("groups").child(groupID).child("tasks");
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String,Object> tempMembers = members;
                boolean singleUserFlag = false;

                //for when there is only one person in the gorup
                if(tempMembers.size() == 1){
                    singleUserFlag = true;
                }

                //remove the user logged in so that we can display their tasks first
                tempMembers.remove(user);

                //iterate over the usernames in the 'members' map
                Iterator it = tempMembers.entrySet().iterator();
                boolean notUser = false;
                while (it.hasNext() || singleUserFlag) {
                    singleUserFlag = false;
                    String currUser;
                    if(notUser) {
                        Map.Entry pair = (Map.Entry) it.next();
                        currUser = (String) pair.getKey();
                    }else{
                        currUser = user;
                    }

                    //create an inner layout to be placed in the master layout
                    final LinearLayout innerLayout = new LinearLayout(TaskActivity.this);
                    innerLayout.setOrientation(LinearLayout.VERTICAL);
                    innerLayout.setPadding(0,50,0,0);


                    //create a layout that will hold the username as subtitle
                    final LinearLayout userView = new LinearLayout(TaskActivity.this);
                    userView.setOrientation(LinearLayout.HORIZONTAL);
                    userView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    TextView userTitle = new TextView(TaskActivity.this);
                    userTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                                            LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                    userTitle.setText(currUser);
                    userTitle.setTextSize(50);
                    userView.addView(userTitle);


                    //Linear layout for popup dialog box
                    LinearLayout lay = new LinearLayout(TaskActivity.this);
                    lay.setPadding(40,100,40,10);
                    final EditText input = new EditText(TaskActivity.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    input.setPadding(5,30,5,30);
                    input.setLayoutParams(lp);
                    lay.addView(input);

                    //create popup dialog box for adding tasks
                    final String tempUsername = currUser;
                    final AlertDialog.Builder builder = new AlertDialog.Builder(TaskActivity.this);
                    input.setText("");
                    builder.setTitle("Add Task for " + currUser);
                    builder.setView(lay);
                    builder.setCancelable(false);
                    builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            addTask(tempUsername, input.getText().toString());
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            dialog.dismiss();
                            dialog.cancel();
                        }
                    });
                    final AlertDialog alertDialog =  builder.create();


                    //create button that will open dialog box
                    Button addButton = new Button(TaskActivity.this);
                    addButton.setText("Add New Task");
                    addButton.setBackgroundResource(R.drawable.button_background);
                    addButton.setTextColor(Color.parseColor("#ffffff"));
                    addButton.setTextSize(15);
                    addButton.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            alertDialog.show();
                        }
                    });
                    userView.addView(addButton);
                    innerLayout.addView(userView);

                    //iterate over this user's tasks
                    ArrayList<String> temp = (ArrayList<String>)dataSnapshot.child(currUser).getValue();
                    for(int i = 1; i < temp.size(); ++i){
                        String task = temp.get(i);
                        LinearLayout taskLayout;

                        //Display task with a "done" button for user logged in
                        if(currUser.equals(user)) {
                            taskLayout = new LinearLayout(TaskActivity.this);
                            taskLayout.setGravity(Gravity.FILL);
                            taskLayout.setBackgroundResource(R.drawable.in_message);
                            taskLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                                        LinearLayout.LayoutParams.MATCH_PARENT));

                            taskLayout.setOrientation(LinearLayout.HORIZONTAL);
                            taskLayout.setPadding(20,10,0,10);

                            TextView taskText = new TextView(TaskActivity.this);
                            taskText.setText(task);
                            taskText.setTextSize(20);
                            taskText.setGravity(Gravity.CENTER_VERTICAL | Gravity.FILL_VERTICAL);

                            TableLayout.LayoutParams taskTextParam = new TableLayout.LayoutParams(0,
                                    TableLayout.LayoutParams.WRAP_CONTENT, 1f);
                            taskText.setLayoutParams(taskTextParam);

                            taskLayout.addView(taskText);

                            Button done = new Button(TaskActivity.this);
                            done.setGravity(Gravity.CENTER);
                            done.setTextColor(Color.parseColor("#ffffff"));
                            done.setBackgroundResource(R.drawable.btn_accept_background);
                            done.setTextSize(15);
                            done.setText("Done");

                            final String userNameForListener = currUser;
                            final String taskForListener = task;
                            final LinearLayout taskLayout2 = taskLayout;
                            done.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    completeTask(userNameForListener, taskForListener);
                                    //taskLayout2.setVisibility(LinearLayout.GONE);
                                }
                            });
                            taskLayout.addView(done);
                        }
                        //display task without a "done" button for other users
                        else{
                            taskLayout = new LinearLayout(TaskActivity.this);
                            taskLayout.setOrientation(LinearLayout.HORIZONTAL);
                            taskLayout.setGravity(Gravity.FILL);
                            taskLayout.setBackgroundResource(R.drawable.in_message);
                            taskLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT));
                            taskLayout.setPadding(10, 20, 0, 20);

                            TextView taskText = new TextView(TaskActivity.this);
                            taskText.setText(task);
                            taskText.setTextSize(20);
                            taskText.setGravity(Gravity.CENTER_VERTICAL | Gravity.FILL_VERTICAL);

                            LinearLayout.LayoutParams taskTextParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                            taskText.setLayoutParams(taskTextParam);

                            taskLayout.addView(taskText);
                        }
                        innerLayout.addView(taskLayout);
                    }

                    //add the inner layout to the master layout
                    layout.addView(innerLayout);


                    if(notUser) {
                        it.remove();
                    }
                    notUser = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        tRef.addListenerForSingleValueEvent(listener);
        tRef.removeEventListener(listener);

    }


    /**
     * Add a task to the database
     *
     * @param username username to add task to in the database
     * @param task the task itself; cannot be empty, but will diplay an error
     */
    public void addTask(String username, String task){
        //Checks to make sure the task is not empty
        if(!task.equals("")) {
            final DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference().child("groups").child(groupID).child("tasks");
            final String tempUsername = username;
            final String tempTask = task;

            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //grab users current tasks
                    ArrayList<String> tasks = (ArrayList<String>) dataSnapshot.child(tempUsername).getValue();

                    //add new task
                    tasks.add(tempTask);

                    //upload as a map to the database
                    Map<String, Object> map = new HashMap<>();
                    map.put(tempUsername, tasks);
                    tasksRef.updateChildren(map);

                    //restart activity to refresh page
                    restartActivity();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            tasksRef.addListenerForSingleValueEvent(listener);
            tasksRef.removeEventListener(listener);
        }
        //If task is empty, will display a toast that says it was unsuccessful
        else{
            Toast toast = Toast.makeText(TaskActivity.this, "Cannot create empty task.",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();

        }
    }


    /**
     * "Complete" task by removing it from the database
     *
     * @param username username for the user completing tasks
     * @param task task to be marked as done
     */
    public void completeTask(String username, String task){
        final String tempTask = task;
        final String tempUsername = username;
        final DatabaseReference tRef = FirebaseDatabase.getInstance().getReference().child("groups").child(groupID).child("tasks");
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> tasks = (ArrayList<String>)dataSnapshot.child(tempUsername).getValue();
                tasks.remove(tempTask);
                Map<String, Object> map = new HashMap<>();
                map.put(tempUsername, tasks);
                tRef.updateChildren(map);
                restartActivity();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        tRef.addListenerForSingleValueEvent(listener);
        tRef.removeEventListener(listener);
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent goBack = new Intent(this, NavDrawerActivity.class);
                goBack.putExtra("username", (String)extras.get("username"));
                if(!this.groupID.equals("") && !this.groupID.equals("requested")) {
                    stopRefreshListener();
                }
                startActivity(goBack);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Stop listener and recreate
     */
    public void restartActivity(){
        stopRefreshListener();
        recreate();
    }


    /**
     * Start listener to see if the database has changed
     */
    public void startRefreshListener(){
        //user ref
        refreshListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!hasRefreshButton && needsRefreshButton) {
                    //add button
                    addRefreshButton();

                    //display toast
                    Toast toast = Toast.makeText(TaskActivity.this, "Changes have been made, please tap the refresh button below before continuing",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }

                needsRefreshButton = true;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ref.addValueEventListener(refreshListener);
    }


    /**
     * Stop the listener
     */
    public void stopRefreshListener(){
        ref.removeEventListener(refreshListener);
    }


    /**
     * Add refresh button at the bottom of the screen
     * if it hasn't been added yet
     */
    public void addRefreshButton() {
        if (!refreshButtonAdded) {
            LinearLayout buttonLayout = new LinearLayout(this);
            buttonLayout.setPadding(16, 16, 16, 16);

            Button button = new Button(this);
            button.setText("Refresh");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    restartActivity();
                }
            });

            buttonLayout.addView(button);

            layout.addView(buttonLayout);
            refreshButtonAdded = true;
        }
    }
}
