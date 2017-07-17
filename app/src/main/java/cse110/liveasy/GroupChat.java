package cse110.liveasy;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Actictivity that controlls the Group Chat feature of the application.
 */
public class GroupChat extends AppCompatActivity {

    private Button sendMsgBtn;
    private EditText inputMsg;
    private ScrollView chatScrollView;
    private LinearLayout msgLinearLay;

    private String username, groupID;
    private DatabaseReference chatRoomRef;
    private String tempKey;
    private String chatMsg, chatUsername;

    private ChildEventListener msgsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Keep keyboard from popping out automatically
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // Get Buttons, layouts, and views from nav view
        sendMsgBtn = (Button) findViewById(R.id.send_msg_btn);
        inputMsg = (EditText) findViewById(R.id.msg_input);
        chatScrollView = (ScrollView) findViewById(R.id.chat_scroll);
        msgLinearLay = (LinearLayout) findViewById(R.id.msg_lin_layout);


        // initialize the user and group id
        username = getIntent().getExtras().get("username").toString();
        groupID = getIntent().getExtras().get("group_id").toString();

        // set the title of the activity
        setTitle( " Chat Room ");

        // Have the scroll view start at the bottom
        chatScrollView.post(new Runnable() {

            @Override
            public void run() {
                chatScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

        // Get the chat room reference from the user's group
        chatRoomRef = FirebaseDatabase.getInstance().getReference().child("groups").child(groupID).child("chat_room");

        // When the send button is clicked
        sendMsgBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                // No empty texts will be sent
                if(inputMsg.getText().toString().equals("")) return;
                // put the message key in the user's group chat room database reference
                tempKey = chatRoomRef.push().getKey();

                // Put the user and message into a map to send to database chat room reference
                DatabaseReference messagesRoot = chatRoomRef.child(tempKey);
                Map<String, Object> userMessageMap = new HashMap<String, Object>();
                userMessageMap.put("name", username);
                userMessageMap.put("msg", inputMsg.getText().toString());

                // reset what user has written
                inputMsg.setText("");

                // update the database
                messagesRoot.updateChildren(userMessageMap);
            }
        });

        // listen for added changes on the database chat room
        msgsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // When a new message is added to the database, it will be appended to the view
                appendChatConversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                appendChatConversation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        // add the child to listen on the chat room reference
        chatRoomRef.addChildEventListener(msgsListener);

    }

    /*
    Appends the messages to the view
     */
    private void appendChatConversation(DataSnapshot dataSnapshot){
        // Iterator used to iterate over the messages in the database
        Iterator iter = dataSnapshot.getChildren().iterator();

        //Loop to iterate over messages in the database and set them to the view
        while(iter.hasNext()){

            chatMsg = (String) ((DataSnapshot)iter.next()).getValue();
            chatUsername = (String) ((DataSnapshot)iter.next()).getValue();

            // Create a new Linear layout
            LinearLayout msgLay = new LinearLayout(GroupChat.this);
            msgLay.setOrientation(LinearLayout.HORIZONTAL);
            msgLay.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));




            // set the message to the view
            if(chatUsername.equals(username) ) {
                // no empty messages will be showed
                if( !chatMsg.equals("")) {
                    TextView msgTextView = new TextView(GroupChat.this);
                    msgTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));


                    msgTextView.setBackgroundResource(R.drawable.out_message);

                    // Put the user's message to the right of the screen
                    msgTextView.setText(chatMsg + "\n");
                    msgTextView.setTextSize(20);
                    msgTextView.setPadding(30, 10, 30, 10);
                    msgTextView.setGravity(Gravity.RIGHT);

                    // add the text view message to the linear layout
                    msgLay.addView(msgTextView);
                    msgLay.setGravity(Gravity.RIGHT);

                    // add inner layout to outer linear layout
                    msgLinearLay.addView(msgLay);
                }



            } else{
                // no empty messages will be showed
                if(!chatMsg.equals("")) {
                    TextView userNameTextView = new TextView(GroupChat.this);
                    userNameTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));

                    // Put the group members to the left of the screen with group member to the left of
                    // chat bubble
                    userNameTextView.setText(chatUsername);
                    userNameTextView.setTextSize(15);
                    userNameTextView.setTypeface(null, Typeface.BOLD);

                    //  add username text view to linear layout
                    msgLay.addView(userNameTextView);

                    // text view for text bubble
                    TextView msgTextView = new TextView(GroupChat.this);
                    msgTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));

                    // sets text message to the left of the screen with in message bubble
                    msgTextView.setBackgroundResource(R.drawable.in_message);
                    msgTextView.setText(chatMsg);
                    msgTextView.setTextSize(20);
                    msgTextView.setPadding(30, 10, 30, 10);
                    msgTextView.setGravity(Gravity.LEFT);

                    // ads message text view to the inner linear layout
                    msgLay.addView(msgTextView);
                    msgLay.setGravity(Gravity.LEFT);

                    // adds inner layout to the outer linear layout
                    msgLinearLay.addView(msgLay);
                }

            }




        }
        // Keeps scroll view at the bottom
        chatScrollView.postDelayed(new Runnable() {

            @Override
            public void run() {
                chatScrollView.fullScroll(View.FOCUS_DOWN);
            }
        }, 100);

    }

    /*
    Controls the back arrow button on top of the activity
    When it is pressed we will go to the nav drawer activity.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent goBack = new Intent(this, NavDrawerActivity.class);
                goBack.putExtra("username", username);
                startActivity(goBack);
                chatRoomRef.removeEventListener(msgsListener);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /*
    When back is pressed we will go to the nav drawer activity
     */
    @Override
    public void onBackPressed(){

    }


}

