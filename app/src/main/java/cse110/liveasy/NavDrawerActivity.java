package cse110.liveasy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import de.hdodenhof.circleimageview.CircleImageView;

public class NavDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Gets a database reference
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();

    // Declares variables to get user info from database
    String username = "";
    final User user = new User();
    final Group group = new Group();
    boolean currentPending = false;
    Boolean hasRequestedGroup = false;
    int pendingSize;
    int memberCount;
    int count = 0;
    int backcount = 0;

    // Declares listeners to be able to remove at the end when going to another activity
    ValueEventListener groupListener;
    ValueEventListener userListener;

    // Declares listenres to set visible upon group status
    MenuItem groupChatItem;
    MenuItem removeUserItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nav_drawer);

        // Get the username
        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");

        // Get the navigation view to get the items inside
        NavigationView navView = (NavigationView)findViewById(R.id.nav_view);
        Menu navMenu = navView.getMenu();
        groupChatItem = navMenu.findItem(R.id.group_chat);
        removeUserItem = navMenu.findItem(R.id.remove_user);

        // Gets the tool bar object for use
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Gets the Drawer layout object for use
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Set toggle ability for nav drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // set the listener for the navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /***************************/

        // Gets the user reference from database
        DatabaseReference uRef = ref.child("users").child(username);
        // Set up listener
        ValueEventListener listener = new ValueEventListener() {

            /*
            Look to see if there is changes in the user information in the database
            */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Get the information and set variables
                Boolean user_has_group = (Boolean) dataSnapshot.child("group").getValue();
                String user_group_id = (String) dataSnapshot.child("groupID").getValue();
                String user_email = (String) dataSnapshot.child("email").getValue();
                String user_phone_number = (String) dataSnapshot.child("phone_number").getValue();
                String user_full_name = (String) dataSnapshot.child("full_name").getValue();
                Boolean user_isPending = (Boolean) dataSnapshot.child("isPending").getValue();
                String photo_url = (String) dataSnapshot.child("photo_url").getValue();
                user.groupID = user_group_id;
                user.email = user_email;
                user.phone_number = user_phone_number;
                user.full_name= user_full_name;

                // checks when the user is pending
                user.isPending = user_isPending;
                currentPending = user_isPending; // listen for change

                // When a user is requests to join a group, flag is updated to use to update
                // once they are rejected or accepted
                if( user.groupID.compareTo("requested") == 0 ) {
                    hasRequestedGroup = true;
                }

                // updates the group and photo user variables to use in the activity
                user.group = user_has_group.booleanValue();
                user.photo_url = photo_url;

                // Sets the user picture in the navdrawer
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                View hView =  navigationView.getHeaderView(0);
                TextView nav_email = (TextView)hView.findViewById(R.id.textView);
                nav_email.setText(user.email);
                TextView nav_user = (TextView)hView.findViewById(R.id.textView3);
                nav_user.setText(user.full_name);
                ImageView thumbnail = (ImageView)hView.findViewById(R.id.imageView);
                Picasso.with(NavDrawerActivity.this)
                        .load(photo_url)
                        .resize(150,150)
                        .centerCrop()
                        .into(thumbnail);

                // updates the user for changes
                updateUser();

                // When the user has a group, then the homepage will be updated accordingly
                if (user_has_group) {

                    //This is the second listener for getting the number of members in the group
                    DatabaseReference gRef = ref.child("groups").child(user_group_id);
                    ValueEventListener listener2 = new ValueEventListener() {
                        /*
                        Checks for changes in the users group in the database
                         */
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            // Che
                            if( user.group ) {
                                // Gets the group information
                                Map<String, Object> group_content = (Map<String, Object>) dataSnapshot.child("members").getValue();
                                ArrayList<String> pending = (ArrayList<String>) dataSnapshot.child("pending").getValue();
                                String gname = (String) dataSnapshot.child("name").getValue();
                                Long gnum = (Long) dataSnapshot.child("num_users").getValue();

                                // Gets the apartment address
                                String aptAddy = (String) dataSnapshot.child("address").getValue();
                                group.address = aptAddy;

                                // Gets the group photo
                                String group_photo = (String) dataSnapshot.child("group_photo").getValue();
                                group.photo_url = group_photo;

                                // Update each user's info
                                group.members = group_content;
                                group.pending = pending;
                                group.name = gname;

                                // Gets the number of members in the group
                                if( gnum != null ) {
                                    group.num_users = gnum.intValue();
                                }

                                // Sets the title of the home activity to the group name
                                if (user.group) {
                                    getSupportActionBar().setTitle(group.name);
                                    notificationUp();
                                }
                            }

                            // Sets the fragment depending on how many users are in the group
                            // In other words, the number profiles for each user are displayed
                            // depending on the fragment that is displayed
                            Fragment fragment = null;
                            Long num_users = (Long) dataSnapshot.child("num_users").getValue();
                            memberCount = num_users.intValue();
                            switch(num_users.intValue()) {
                                case 1:
                                    fragment = new Home1();
                                    break;
                                case 2:
                                    fragment = new Home2();
                                    break;
                                case 3:
                                    fragment = new Home3();
                                    break;
                                case 4:
                                    fragment = new Home4();
                                    break;
                                case 5:
                                    fragment = new Home5();
                                    break;
                                default:
                                    fragment = new Home5();
                                    break;
                            }

                            // If not fragment is initialized, sanity check
                            if(fragment != null){
                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.content_frame, fragment);
                                ft.commitNowAllowingStateLoss();
                            }

                            // Gets the drawer object to close
                            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    };
                    // Add the group listener to do the above once then delete it
                    gRef.addListenerForSingleValueEvent(listener2);
                    gRef.removeEventListener(listener2);


                }
                // When there is not more than one person in the group, only the main user is displayed
                else{
                    Fragment fragment = new Home1();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.commitNowAllowingStateLoss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        // add user listener to listen once, then remove the listener
        uRef.addListenerForSingleValueEvent(listener);
        uRef.removeEventListener(listener);


        /***************************/
        // Sets the title of the action bar to the user when they don't have a group
        getSupportActionBar().setTitle(username);

    }

    /*
    Easter egg on back pressed button from android device in home screen
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        else {
            MediaPlayer quack = MediaPlayer.create(this, R.raw.quack);
            MediaPlayer quack1 = MediaPlayer.create(this, R.raw.quack1);
            MediaPlayer quack2 = MediaPlayer.create(this, R.raw.quack2);
            MediaPlayer quack3 = MediaPlayer.create(this, R.raw.quack3);
            MediaPlayer quack4 = MediaPlayer.create(this, R.raw.quack4);
            Random randomGen = new Random();

            int check = backcount%5;

            if (check == randomGen.nextInt(5)) {
                if (check == 0)
                    quack.start();
                if (check == 1)
                    quack1.start();
                if (check == 2)
                    quack2.start();
                if (check == 3)
                    quack3.start();
                if (check == 4)
                    quack4.start();
            }

            backcount++;
        }
    }

    /*
    Adds times to the action bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }

    /*
    The method adds the calendar option to the top right of the actionbar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.go_to_calendar) {
            Intent goToCalendar = new Intent(this, CalendarActivity.class);
            goToCalendar.putExtra("username", username);
            goToCalendar.putExtra("group_id", user.groupID);
            removeAllListeners();
            startActivity(goToCalendar);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    This method handles options from the navigation bar
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // When the group chat option is slected, where you can talk with your members
        if(id == R.id.group_chat){
            Intent goToGroupChat = new Intent(this, GroupChat.class);
            goToGroupChat.putExtra("username", username);
            goToGroupChat.putExtra("group_id", user.groupID);
            removeAllListeners();
            startActivity(goToGroupChat);
            finish();
        }
        // When the share code option is selected, where you can get group key to send to other
        // users
        else if (id == R.id.nav_send) {
            Intent goToShareCode = new Intent(this, ShareGroupCode.class);
            goToShareCode.putExtra("username", username);
            goToShareCode.putExtra("group_id", user.groupID);
            removeAllListeners();
            startActivity(goToShareCode);
            finish();


        }
        // When the manage requests option is selected, where you can accept and reject users
        else if (id == R.id.manage_requests){
            Intent goToRequests = new Intent(this, ManageRequests.class);
            goToRequests.putStringArrayListExtra("pending", group.pending);
            goToRequests.putExtra("username", getIntent().getExtras().getString("username"));
            goToRequests.putExtra("groupKey", user.groupID);
            removeAllListeners();
            startActivity(goToRequests);
            finish();
        }
        // When the remove user option is selected, where you can vote out specific users
        else if ( id == R.id.remove_user ) {
            Intent goToRemoveUser = new Intent(this, RemoveUserFromGroup.class);
            goToRemoveUser.putExtra("username", getIntent().getExtras().getString("username"));
            goToRemoveUser.putExtra("groupID", user.groupID);
            removeAllListeners();
            startActivity(goToRemoveUser);
            finish();

        }
        // When the manage tasks option is selected, where you can add or finish, tasks
        else if(id == R.id.manage_tasks){
            Intent goToManageTasks = new Intent(this, TaskActivity.class);
            goToManageTasks.putExtra("username", username);
            goToManageTasks.putExtra("group_id", user.groupID);
            goToManageTasks.putExtra("members", (HashMap)group.members);
            removeAllListeners();
            startActivity(goToManageTasks);
            finish();
        }
        // When the user chooses to leave the group option
        else if ( id == R.id.leave_group ){
            // Pop up confirmation is display
            View v = findViewById(R.id.content_nav_drawer);
            AlertDialog.Builder displayConfirmation  = new AlertDialog.Builder(v.getContext());
            displayConfirmation.setMessage("Are you sure you want to leave this group?" +
                    "\nIf you are the last member, the group will be deleted");
            displayConfirmation.setTitle("Leave group");
            displayConfirmation.setPositiveButton("Yes", null);
            displayConfirmation.setNegativeButton("No", null);
            displayConfirmation.setCancelable(false);

            // When the user selects yes
            displayConfirmation.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                       // Listenes for the yes on the pop up comfirmation
                        public void onClick(DialogInterface dialog, int which) {
                            // When the user does ahve a group, they will be rmoved from the group
                            // and a toast will be display
                            if( user.group ) {
                                removeUserFromGroup(username);
                                Toast toast = Toast.makeText(NavDrawerActivity.this, "You have been removed" +
                                                " from the group.",
                                        Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.NO_GRAVITY, 0, 0);
                                toast.show();
                            }
                            // When the user does not have a group, toast tells him so
                            else {
                                Toast toast = Toast.makeText(NavDrawerActivity.this, "You do not have a group.",
                                        Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.NO_GRAVITY, 0, 0);
                                toast.show();
                            }
                        }
                    });
            // When the No is selected on the pop up
            displayConfirmation.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //do nothing
                        }
                    });
            displayConfirmation.create().show();
        }
        // When the lougout option is selected on the nav drawer
        else if (id == R.id.logout){

            // The user preferences will be cleared from the device, so user does not log in
            // autmatically anymore
            SharedPreferences sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.commit();

            // Get firebase instance and log it out
            FirebaseAuth.getInstance().signOut();

            // Goes to the login activity after logout
            Intent goToLogin = new Intent(this, LoginActivity.class);
            removeAllListeners();
            startActivity(goToLogin);
            finish();
        }
        // Option to close the drawer layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /*
    Method that goes to page to create a group
     */
    public void goToCreateGroup(View view){
        Intent goToCreateGroup = new Intent(this, CreateGroup.class);

        Bundle extras = this.getIntent().getExtras();

        if (extras != null) {
            String value = extras.getString("username");
            goToCreateGroup.putExtra("username", value);
        }

        removeAllListeners();

        startActivity(goToCreateGroup);
        finish();
    }

    /*
    Method that goes to page so user can join a group
     */
    public void goToJoinGroup(View view){
        Bundle extras = this.getIntent().getExtras();
        Intent goToJoinGroup = new Intent(this, JoinGroup.class);
        goToJoinGroup.putExtra("username", (String)extras.getString("username"));
        removeAllListeners();
        startActivity(goToJoinGroup);
        finish();

    }

    /*
    Method to update the group information to use at this stage of the group
     */
    public void updateGroup(){

        // Only when the user has a group, will the group be updated
        if(user.group) {
            // Gets the group reference from the database
            DatabaseReference gRef = ref.child("groups").child(user.groupID);
            groupListener = new ValueEventListener() {

                /*
                Method to update the group when there is changes in the database
                 */
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    // Listenes for the group, and checks again if the user has a group, so listener
                    // only listenes when the user actually has a group
                    if( user.group ) {

                        // Get a map of the group information
                        Map<String, Object> group_content = (Map<String, Object>) dataSnapshot.child("members").getValue();
                        ArrayList<String> pending = (ArrayList<String>) dataSnapshot.child("pending").getValue();
                        String gname = (String) dataSnapshot.child("name").getValue();
                        Long gnum = (Long) dataSnapshot.child("num_users").getValue();

                        // Update each user's info
                        group.members = group_content;
                        group.pending = pending;
                        group.name = gname;

                        // When the group number is not null
                        if( gnum != null ) {
                            // If the number of users is not the same, then we will restart
                            // the activity
                            if ( group.num_users != gnum.intValue() ) {

                                restartActivity();
                            }
                        }
                        // Again if the user has a group, then some options in the navbar will be
                        // disabled
                        if (user.group) {

                            groupChatItem.setEnabled(true);
                            groupChatItem.setVisible(true);
                            removeUserItem.setEnabled(true);
                            removeUserItem.setVisible(true);
                            // Sets the title of the action bar to be the group name
                            getSupportActionBar().setTitle(group.name);
                            // Calls method to see whether to notify user of pending requests or not
                            notificationUp();
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            // Adds the group listener to the group reference
            gRef.addValueEventListener(groupListener);

        }
    }

    /***********************************************************/

    /*
    Method handles selecting a profile
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void toProfilePopup(View view, Profile memberContent, final String memberName) {

        // Sets up an alert dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (this).getLayoutInflater();

        // The diaog view is set and the text view as well
        View dialog_view = inflater.inflate(R.layout.activity_popup_profile, null);
        TextView users_name = (TextView)dialog_view.findViewById(R.id.username);
        users_name.setText(memberName);

        // Gets picture from database using Picasso library, for faster loads and caches
        CircleImageView selfie = (CircleImageView)dialog_view.findViewById(R.id.profile_image_popup);
        Picasso.with(this)
                .load(memberContent.photo_url)
                .resize(200,200)
                .centerCrop()
                .placeholder(R.drawable.blank)
                .into(selfie);


        // Sets the email on the profile
        TextView email = (TextView)dialog_view.findViewById(R.id.email);
        email.setText(memberContent.email);

        // Sets the phone number on the profile
        TextView phoneNum = (TextView)dialog_view.findViewById(R.id.phone_number);
        String formattedNumber = PhoneNumberUtils.formatNumber(memberContent.phoneNum, "US");
        phoneNum.setText(formattedNumber);

        // When the user clicks ok on the profile
        builder.setPositiveButton(R.string.go_back, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Simply goes back to homepage
            }
        });

        // When the profile button is selected, we go to the users profile
        builder.setNegativeButton(R.string.go_profile, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent goProfile = new Intent(NavDrawerActivity.this, ProfileActivity.class);
                goProfile.putExtra("username", username);
                goProfile.putExtra("memberName", memberName);
                goProfile.putExtra("group", user.group);
                goProfile.putExtra("groupID",user.groupID);
                removeAllListeners();
                startActivity(goProfile);
                finish();
            }
        });
        // the view is set and shown
        builder.setView(dialog_view);
        builder.create().show();

    }

    public void toGroupProfilePopup(View view) {

        // When the group profile is clicked we go to the group profile acitivity
        Intent goProfile = new Intent(NavDrawerActivity.this, GroupProfileActivity.class);
        goProfile.putExtra("username", username);
        goProfile.putExtra("groupName", group.name);
        goProfile.putExtra("groupKey", user.groupID);
        removeAllListeners();
        startActivity(goProfile);
        finish();
    }

    /*
    When there is pending requests, a user will get an in app notification
     */
    public void notificationUp() {

        // Only when the user has a group, will any of this be checked
        if (user.group) {
            // Gets a reference to the pending requests field in the database from the group
            DatabaseReference pRef = ref.child("groups").child(user.groupID).child("pending");

            // Listenes for changes in the pending requests
            ValueEventListener listener = new ValueEventListener() {
                /*
                When there is a change the user will be notified and the nav drawer updated with
                the information
                 */
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if( user.group ) {
                        // Look through the users that are pending
                        ArrayList<String> list = (ArrayList<String>) dataSnapshot.getValue();
                        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                        Menu menu = navigationView.getMenu();
                        MenuItem requestItem = menu.findItem(R.id.manage_requests);

                        // When there is more than one pending request the number of pending
                        // requests will appear in the nav drawer
                        if (list.size() > 1) {
                            requestItem.setTitle("Manage Requests (" + (list.size() - 1) + ")");

                        }
                        // Else, no info is displayed
                        else {
                            requestItem.setTitle("Manage Requests");
                        }
                        // Used to check when the number of pending requests has changed
                        if (list.size() != pendingSize) {
                            // When there is more than one pending request
                            if (list.size() > 1) {
                                // When the acitivity has not finished yet, then we will give an
                                // in app notification stating how many pending requests the user
                                // has
                                if(!isFinishing()) {
                                    Toast toast = Toast.makeText(NavDrawerActivity.this, "You have " + (list.size() - 1) + " pending request(s)",
                                            Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.NO_GRAVITY, 0, 0);
                                    toast.show();
                                }
                            }
                            // The pending size is reset to new value to check if more changes are available
                            pendingSize = list.size();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            // Adds listener for single check, when the activity is opened and then removed
            pRef.addListenerForSingleValueEvent(listener);
            pRef.removeEventListener(listener);
        }
    }

    /*
    Method used to remove the user when they select to leave the group
     */
    public void removeUserFromGroup(final String userName){

        // Gets a group database reference
        final DatabaseReference gRef = ref.child("groups").child(user.groupID);
        gRef.removeEventListener(groupListener);
        user.group = false;
        user.groupID = "";

        groupListener = null;

        // Listenes for group changes in the database
        ValueEventListener listener = new ValueEventListener() {
            /*
            When the user leaves the group, the home page will be fixed accordingly
            */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Gets a user database reference
                DatabaseReference uRef = ref.child("users").child(username);
                Map<String,Object> userContents = new HashMap<String,Object>();
                userContents.put("group", new Boolean(false));
                userContents.put("groupID", "");
                uRef.updateChildren(userContents);

                // Gets group info
                Map<String, Object> group = (HashMap<String,Object>)dataSnapshot.getValue();

                // Gets the members information of the group and removes the member to remove
                Map<String,Object> members = (HashMap<String,Object>)dataSnapshot.child("members").getValue();
                members.remove(userName);

                //remove user from tasks
                Map<String,Object> tasks = (HashMap)group.get("tasks");
                tasks.remove(userName);
                group.put("tasks", tasks);

                // Gets new number of users and either wipes out the group if the leaving group
                // was the last user, or just decreases the number of members in the gruoup
                int currentMembers = ((Long)dataSnapshot.child("num_users").getValue()).intValue();
                if(currentMembers > 1) {
                    group.put("members", members);
                    group.put("num_users", currentMembers - 1);
                    gRef.updateChildren(group);
                }else{
                    gRef.removeValue();
                }

                restartActivity();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        // Adds and remove listener for single time use when activity starts
        gRef.addListenerForSingleValueEvent(listener);
        gRef.removeEventListener(listener);

    }

    /*
    Restarts the activity when necessary, but only if the activity has not yet been finished
     */
    public void restartActivity() {
        if(!isFinishing()) {
            Intent restartActivity = new Intent(NavDrawerActivity.this, NavDrawerActivity.class);
            restartActivity.putExtra("username", username);
            removeAllListeners();
            restartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(restartActivity);
            finish();
        }
    }
    /*
    Updates the user when there is changes to their information
     */
    public void updateUser(){
        DatabaseReference uRef = ref.child("users").child(username);

         userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // All the fields are updated
                Boolean user_has_group = (Boolean) dataSnapshot.child("group").getValue();
                String user_group_id = (String) dataSnapshot.child("groupID").getValue();
                String user_email = (String) dataSnapshot.child("email").getValue();
                String user_phone_number = (String) dataSnapshot.child("phone_number").getValue();
                String user_full_name = (String) dataSnapshot.child("full_name").getValue();
                Boolean user_isPending = (Boolean) dataSnapshot.child("isPending").getValue();
                user.groupID = user_group_id;
                user.group = user_has_group.booleanValue();
                user.email = user_email;
                user.phone_number = user_phone_number;
                user.full_name= user_full_name;
                user.isPending = user_isPending;

                // Only when they have a group, will their group info also be updated
                if( user.group && groupListener == null) {
                    updateGroup();
                }

                // notify user is they were rejected or accepted
                if( currentPending != user.isPending ) {

                    // user has requested group and now has a group
                    if ( hasRequestedGroup && user.group ) {

                        // check if this activity is not a background activity
                        if ( !isFinishing() ) {

                            // update user object
                            currentPending = user_isPending;

                            // Give the user a welcome to the group pop up
                            View v = findViewById(R.id.content_nav_drawer);
                            AlertDialog.Builder displayConfirmation = new AlertDialog.Builder(v.getContext());
                            displayConfirmation.setMessage("Welcome!");
                            displayConfirmation.setTitle("You have been accepted :D !");
                            displayConfirmation.setCancelable(false);

                            displayConfirmation.setPositiveButton("Awesome",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            restartActivity();
                                        }
                                    });
                            displayConfirmation.create().show();

//                            // display a message to the user that they have been accepted
//                            Toast toast = Toast.makeText(NavDrawerActivity.this, "Welcome, You have been ACCEPTED! :D",
//                                    Toast.LENGTH_SHORT);
//                            toast.setGravity(Gravity.NO_GRAVITY, 0, 0);
//                            toast.show();
                        }

                    } else if ( hasRequestedGroup && !user.group ) {

                        // check if this activity is not a background activity
                        if( !isFinishing() ) {
                            currentPending = user_isPending;
                            View v = findViewById(R.id.content_nav_drawer);
                            AlertDialog.Builder displayConfirmation = new AlertDialog.Builder(v.getContext());
                            displayConfirmation.setMessage("You have been REJECTED! D:");
                            displayConfirmation.setTitle("We regret to inform...");
                            displayConfirmation.setCancelable(false);

                            displayConfirmation.setPositiveButton("Aw Shucks",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            restartActivity();
                                        }
                                    });
                            displayConfirmation.create().show();
                        }

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        // Adds the event listener to keep on listening
        uRef.addValueEventListener(userListener);
    }

    /*
    Method to remove all the lisnteres
     */
    public void removeAllListeners() {

        DatabaseReference uRef = ref.child("users").child(username);
        uRef.removeEventListener(userListener);
        if( user.group ) {
            DatabaseReference gRef = ref.child("groups").child(user.groupID);
            gRef.removeEventListener(groupListener);
        }

    }

    /*
    Method to get the array of members to use throughout the file
     */
    public String[] getMembers() {

        String[] mems = new String[group.num_users];
        int i = 0;
        for (Map.Entry<String, Object> entry : group.members.entrySet()) {

            mems[i] = entry.getKey();
            i++;
        }

        return mems;
    }
}
