package cse110.liveasy;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.content.Intent;
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
 * Activity that controlls the join a group logic of the application
 */
public class JoinGroup extends AppCompatActivity {

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        //grab username passed in with the intent
        Bundle extras = getIntent().getExtras();
        username = extras.get("username").toString();
        }

    /**
     * Override this method so that pressing back does nothing
     */
    @Override
    public void onBackPressed(){

    }

    /**
     * Will attempt to add the current user to the group specified by the key passed
     * in through the EditText
     * @param view context
     */
    public void joinGroup(View view) {
        //grab the EditText with the user key typed in
        EditText editText = (EditText) findViewById(R.id.editText6);
        final String groupKey = editText.getText().toString();

        //database reference
        FirebaseDatabase ref = FirebaseDatabase.getInstance();
        final DatabaseReference gRef = ref.getReference().child("groups");

        final DatabaseReference uRef = ref.getReference().child("users").child(username);

        final View tempView = view;

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //if key is valid
                if(!groupKey.equals("") && (Boolean)dataSnapshot.hasChild(groupKey)){

                    //add to pending in group
                    ArrayList<String> current = (ArrayList<String>) dataSnapshot.child(groupKey).child("pending").getValue();
                    current.add(username);

                    Map<String, Object> user_pending = new HashMap<String, Object>();
                    user_pending.put("/isPending/", new Boolean(true));
                    uRef.updateChildren(user_pending);


                    // flag user as they requested
                    Map<String, Object> user_requested_group = new HashMap<String, Object>();
                    user_requested_group.put("/groupID/", new String("requested") );
                    uRef.updateChildren(user_requested_group);

                    Map<String,Object> map = new HashMap<String,Object>();
                    map.put("pending", current);
                    gRef.child(groupKey).updateChildren(map);

                    Context context = tempView.getContext();
                    LinearLayout layout = new LinearLayout(context);
                    layout.setOrientation(LinearLayout.VERTICAL);


                    //TextView for the success method
                    TextView message = new TextView(tempView.getContext());
                    message.setText("Your request has been sent to join the group!");
                    message.setTextSize(17);
                    message.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

                    layout.addView(message);


                    //Create alert dialog that will tell the user a request has been sent
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinGroup.this);
                    builder.setView(layout);
                    builder.setTitle("Request Sent");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            Intent goBack = new Intent(JoinGroup.this, NavDrawerActivity.class);
                            goBack.putExtra("username", username);
                            startActivity(goBack);
                            finish();
                        }
                    });
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent goBack = new Intent(JoinGroup.this, NavDrawerActivity.class);
                            goBack.putExtra("username", username);
                            startActivity(goBack);
                            finish();
                        }
                    });
                    builder.create().show();
                }
                //re-enter key if it is not valid
                else{
                    Toast toast = Toast.makeText(JoinGroup.this, "Invalid Key, Please Re-Enter",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        gRef.addListenerForSingleValueEvent(listener);
        gRef.removeEventListener(listener);

    }

    /**
     * Cancel and go back to the home page
     *
     * @param view context
     */
    public void cancelJoinGroup(View view){
        Intent goBacktoMain = new Intent(this, NavDrawerActivity.class);
        goBacktoMain.putExtra("username", username);
        startActivity(goBacktoMain);
        finish();
    }
    }


