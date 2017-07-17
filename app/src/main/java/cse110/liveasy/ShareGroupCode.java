package cse110.liveasy;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 *Activity that controls sharing a group code, where the user can copy it to the clipboard and
 *share it accordingly
 */
public class ShareGroupCode extends AppCompatActivity {
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_group_code);

        //get bundle passed through
        extras = getIntent().getExtras();
        String groupKey = extras.getString("group_id");

        //get textview that should display group code
        TextView groupID = (TextView)findViewById(R.id.group_id);

        //check to see if user is in a group
        if(!groupKey.equals("")) {
            groupID.setText(extras.getString("group_id"));
            groupID.setTextSize(28);
        }
        //if user is not in a group display message instead
        else{
            groupID.setText("No group code to share.");
            groupID.setTextSize(28);
            LinearLayout buttonLayout = (LinearLayout)findViewById(R.id.button_layout);
            buttonLayout.setVisibility(LinearLayout.GONE);
        }

        //set back button to go back to homepage
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent goBack = new Intent(this, NavDrawerActivity.class);
                goBack.putExtra("username", extras.getString("username"));
                startActivity(goBack);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Copy the group ID to clipboard
     *
     * @param view context for the activity
     */
    public void copyToClipboard(View view){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied", extras.getString("group_id"));
        clipboard.setPrimaryClip(clip);

        Toast toast = Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 400);
        toast.show();
    }

    @Override
    public void onBackPressed(){
        //override this method to do nothing when the back button is pressed
    }
}
