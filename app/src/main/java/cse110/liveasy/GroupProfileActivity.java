package cse110.liveasy;

/*
This file uses source from stack overflow for orienting the image
http://stackoverflow.com/questions/20478765/how-to-get-the-correct-orientation-of-the-image-selected-from-the-default-image
This file uses the tutorial source code for camera permission:
https://developer.android.com/training/permissions/requesting.html
 */

/* SOURCES:

   The following sources were utilized for taking a profile photo:

   http://www.codepool.biz/take-a-photo-from-android-camera-and-upload-it-to-a-remote-php-server.html
   http://www.codepool.biz/take-a-photo-from-android-camera-and-upload-it-to-a-remote-php-server.html
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// Source: https://github.com/hdodenhof/CircleImageView
import de.hdodenhof.circleimageview.CircleImageView;



public class GroupProfileActivity extends AppCompatActivity {

    Bundle extras;

    // Profile fields
    String changedGroupAddress;
    String originalGroupAddress;
    String key;
    boolean changeFlag;
    Map<String, Object> groupMapCopy;

    // Camera fields
    private static final String TAG = "upload";
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;
    String url = "";
    File photoFile = null;
    Bitmap bitmap = null;
    Boolean changedPic = false;
    ProgressDialog progressDialog;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    public static final int MY_PERMISSIONS_REQUEST_WRITE = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);

        extras = getIntent().getExtras();

        key = (String) extras.get("groupKey");

        // Init support bar
        getSupportActionBar().setTitle((String) extras.get("groupName"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final FirebaseDatabase ref = FirebaseDatabase.getInstance();
        final DatabaseReference uRef = ref.getReference().child("groups").child(key);

        // Use listener to populate profile
        ValueEventListener groupListener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Map<String, Object> groupMap = (HashMap<String, Object>) dataSnapshot.getValue();
                groupMapCopy = groupMap;
                changeFlag = false;

                // Populate group address field
                TextView addy = (TextView) findViewById(R.id.address);
                addy.setText((String) groupMap.get("address"));
                originalGroupAddress = addy.getText().toString();

                // Populate apartment image view
                CircleImageView apt = (CircleImageView) findViewById(R.id.apt_image);
                Picasso.with(GroupProfileActivity.this)
                        .load((String)groupMap.get("group_photo"))
                        .resize(200,200)
                        .centerCrop()
                        .placeholder(R.drawable.blank)
                        .into(apt);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        uRef.addListenerForSingleValueEvent(groupListener);
        uRef.removeEventListener(groupListener);

        // Set listener for apartment image
        CircleImageView apt_image = (CircleImageView) findViewById(R.id.apt_image);
        apt_image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                // Set Permissions
                readPermission();
                writePermission();
                cameraPermission();


                return false;
            }
        });

        // Set listener for Address TextView
        final TextView groupAddress = (TextView) findViewById(R.id.address);
        final EditText editGroupAddress  = (EditText) findViewById(R.id.edit_address);
        groupAddress.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                // Display the edit text view
                groupAddress.setVisibility(View.GONE);
                editGroupAddress.setVisibility(View.VISIBLE);
                editGroupAddress.setText("");

                editGroupAddress.setOnKeyListener(new View.OnKeyListener() {

                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        changedGroupAddress = editGroupAddress.getText().toString();

                        // If the event is a key-down event on the "enter" button
                        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                                (keyCode == KeyEvent.KEYCODE_ENTER)) {

                            if(changedGroupAddress.equals("")){
                                groupAddress.setText(originalGroupAddress);
                                groupAddress.setVisibility(View.VISIBLE);
                                editGroupAddress.setVisibility(View.GONE);
                            }
                            else{
                                groupAddress.setText(changedGroupAddress);
                                groupAddress.setVisibility(View.VISIBLE);
                                editGroupAddress.setVisibility(View.GONE);
                                changeFlag = true;
                                originalGroupAddress = changedGroupAddress;
                            }
                            return false;

                        }
                        return false;
                    }
                });
                return false;
            }
        });
    }

    // Use nav drawer to exit profile and prompt user if they want their changes saved
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(changeFlag) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Would you like to save changes made?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    uploadData();
                                    if(changedPic){
                                        progressDialog = new ProgressDialog(GroupProfileActivity.this,
                                                R.style.AppTheme_Dark_Dialog);
                                        progressDialog.setIndeterminate(true);
                                        progressDialog.setMessage("Uploading picture...");
                                        progressDialog.show();
                                        uploadPicAndJump();
                                    }
                                    else {
                                        changeFlag = false;
                                        Intent goBack = new Intent(GroupProfileActivity.this, NavDrawerActivity.class);
                                        goBack.putExtra("username", (String) extras.get("username"));
                                        startActivity(goBack);
                                        finish();
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent goBack = new Intent(GroupProfileActivity.this, NavDrawerActivity.class);
                                    goBack.putExtra("username", (String)extras.get("username"));
                                    startActivity(goBack);
                                    finish();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else
                {
                    Intent goBack = new Intent(GroupProfileActivity.this, NavDrawerActivity.class);
                    goBack.putExtra("username", (String)extras.get("username"));
                    startActivity(goBack);
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Use back button to exit profile and prompt user if they want their changes saved
    public void onBackPressed() {
        if (changeFlag) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Would you like to save changes made?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            uploadData();
                            if(changedPic){
                                progressDialog = new ProgressDialog(GroupProfileActivity.this,
                                        R.style.AppTheme_Dark_Dialog);
                                progressDialog.setIndeterminate(true);
                                progressDialog.setMessage("Uploading picture...");
                                progressDialog.show();
                                uploadPicAndJump();
                            }
                            else{
                                changeFlag = false;
                                Intent goBack = new Intent(GroupProfileActivity.this, NavDrawerActivity.class);
                                goBack.putExtra("username", (String) extras.get("username"));
                                startActivity(goBack);
                                finish();
                            }
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent goBack = new Intent(GroupProfileActivity.this, NavDrawerActivity.class);
                            goBack.putExtra("username", (String) extras.get("username"));
                            startActivity(goBack);
                            finish();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        } else {
            Intent goBack = new Intent(this, NavDrawerActivity.class);
            goBack.putExtra("username", (String) extras.get("username"));
            startActivity(goBack);
            finish();
        }
    }

    // Store profile changes to database
    public void uploadData()
    {
        //uploading...
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference allref = database.getReference().child("groups").child(key);

        groupMapCopy.put("address", originalGroupAddress);

        allref.updateChildren(groupMapCopy);
    }

    // Store apartment image to database and exit profile
    public void uploadPicAndJump(){
        //uploading...
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference().child("groups").child(key);

        // Save image
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = mStorageRef.child(extras.getString("groupKey"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 33, baos);
        byte[] fileData = baos.toByteArray();
        UploadTask uploadTask = profileRef.putBytes(fileData);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                url = downloadUrl.toString();
                groupMapCopy.put("group_photo", url);
                ref.updateChildren(groupMapCopy);

                // Jump to Nav Drawer
                progressDialog.dismiss();
                Intent goBack = new Intent(GroupProfileActivity.this, NavDrawerActivity.class);
                goBack.putExtra("username", (String) extras.get("username"));
                changedPic = false;
                changeFlag = false;
                startActivity(goBack);
                finish();
            }
        });
    }

    // Picture Taking functionality
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        String storageDir = Environment.getExternalStorageDirectory() + "/picupload";
        File dir = new File(storageDir);
        if (!dir.exists())
            dir.mkdir();

        File image = new File(storageDir + "/" + imageFileName + ".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();

        Log.i(TAG, "photo path = " + mCurrentPhotoPath);


        return image;
    }

    private void takePhoto() {

        dispatchTakePictureIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            bitmap = setPic();

        }
    }

    private Bitmap setPic() {

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);


        ExifInterface exif = null;
        try {
            exif = new ExifInterface(mCurrentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap bmRotated = rotateBitmap(bitmap, orientation);

        CircleImageView apt_image = (CircleImageView) findViewById(R.id.apt_image);
        Picasso.with(GroupProfileActivity.this)
                .load(photoFile)
                .resize(200,200)
                .centerCrop()
                .placeholder(R.drawable.blank)
                .into(apt_image);

        changeFlag = true;
        changedPic = true;

        return bmRotated;
    }

    private void cameraPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.CAMERA)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        }
        else{
            takePhoto();
        }
    }

    private void readPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this
                ,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ);

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    private void writePermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this
                ,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)     {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:{
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    takePhoto();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    url = "https://firebasestorage.googleapis.com/v0/b/liveasy-85049.appspot.com/o/woodie.jpg?alt=media&token=87057c2f-1d19-4b23-90f1-1f215b5ad618";
                }
                return;
            }


            case MY_PERMISSIONS_REQUEST_READ:{
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_WRITE:{
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

}
