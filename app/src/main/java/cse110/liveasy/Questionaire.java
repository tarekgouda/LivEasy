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

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.ExifInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.provider.MediaStore;
import android.widget.Button;
import android.graphics.Bitmap;
import android.widget.ImageView;


import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


import java.util.HashMap;
import java.util.Map;

/**
 *Activity that lets a user fill out a questionnaire to later populate a users profile
 */
public class Questionaire extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    public static final int MY_PERMISSIONS_REQUEST_WRITE = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ = 2;

    //Flags to manage custom collapsable views
    Boolean contactIsOpen = false;
    Boolean aboutMeIsOpen = false;
    Boolean preferenceIsOpen = false;
    Boolean petPeevesIsOpen = false;
    Boolean allergiesIsOpen = false;

    //bunle passed in through intent
    Bundle extras;


    private Button mTakePhoto;
    private ImageView mImageView;
    private static final String TAG = "upload";
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;
    String url = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionaire);






        extras = getIntent().getExtras();

        //Collect all subtitle views
        TextView eContactText = (TextView)findViewById(R.id.eContactText);
        eContactText.setText("+ Emergency Contact Info");

        TextView aboutMeText = (TextView)findViewById(R.id.about_me_text);
        aboutMeText.setText("+ About Me");

        TextView preferencesText = (TextView)findViewById(R.id.preferences_text);
        preferencesText.setText("+ Preferences");

        TextView petPeevesText = (TextView)findViewById(R.id.pet_peeves_text);
        petPeevesText.setText("+ Pet Peeves");

        TextView allergiesText = (TextView)findViewById(R.id.allergies_text);
        allergiesText.setText("+ Allergies");



        //Collect all linear layouts for input and hide them
        LinearLayout contactLayout = (LinearLayout)findViewById(R.id.contact_layout);
        contactLayout.setVisibility(LinearLayout.GONE);

        LinearLayout aboutMeLayout = (LinearLayout)findViewById(R.id.about_me_layout);
        aboutMeLayout.setVisibility(LinearLayout.GONE);

        RelativeLayout checkboxes = (RelativeLayout)findViewById(R.id.questionnaire_checkboxes);
        checkboxes.setVisibility(RelativeLayout.GONE);

        LinearLayout petPeevesLayout = (LinearLayout)findViewById(R.id.pet_peeve_layout);
        petPeevesLayout.setVisibility(LinearLayout.GONE);

        LinearLayout allergiesLayout = (LinearLayout)findViewById(R.id.allergies_layout);
        allergiesLayout.setVisibility(LinearLayout.GONE);



        mTakePhoto = (Button) findViewById(R.id.uploadPhotoBtn);
        mImageView = (ImageView) findViewById(R.id.selfie_thumbnail);

        mTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id) {
                    case R.id.uploadPhotoBtn:
                        readPermission();

                        writePermission();

                        cameraPermission();
                        break;
                }
            }
        });


    }

    /**
     *  Checks that all fields have input and stores them in firebase
     */
    public void uploadData(View view){
        //if statement checks to see that all fields are populated
        if(validate(view)){
            TextView currentText;
            String currentString;
            CheckBox currentCheckBox;
            Map<String,Object> preferences = new HashMap<String,Object>();


            /** upload to database **/

            //emergency contact name
            currentText = (TextView)findViewById(R.id.input_emergency_name);
            currentString = currentText.getText().toString();
            preferences.put("e_contact_name", currentString);

            //emergency contact relationship
            currentText = (TextView)findViewById(R.id.input_emergency_relationship);
            currentString = currentText.getText().toString();
            preferences.put("e_contact_relationship", currentString);

            //emergency contact phone number
            currentText = (TextView)findViewById(R.id.input_emergency_phone);
            currentString = currentText.getText().toString();
            preferences.put("e_contact_phone_number", currentString);

            //about me
            currentText = (TextView)findViewById(R.id.input_about_me);
            currentString = currentText.getText().toString();
            preferences.put("about_me", currentString);

            //smoking
            currentCheckBox = (CheckBox)findViewById(R.id.smoking_checkbox);
            if(currentCheckBox.isChecked()){
                preferences.put("smoking", new Boolean(true));
            }
            else{
                preferences.put("smoking", new Boolean(false));
            }

            //drinking
            currentCheckBox = (CheckBox)findViewById(R.id.drinking_checkbox);
            if(currentCheckBox.isChecked()){
                preferences.put("drinking", new Boolean(true));
            }
            else{
                preferences.put("drinking", new Boolean(false));
            }

            //guests
            currentCheckBox = (CheckBox)findViewById(R.id.guests_checkbox);
            if(currentCheckBox.isChecked()){
                preferences.put("guests", new Boolean(true));
            }
            else{
                preferences.put("guests", new Boolean(false));
            }

            //pets
            currentCheckBox = (CheckBox)findViewById(R.id.pets_checkbox);
            if(currentCheckBox.isChecked()){
                preferences.put("pets", new Boolean(true));
            }
            else{
                preferences.put("pets", new Boolean(false));
            }

            //pet peeves
            currentText = (TextView)findViewById(R.id.input_pet_peeve);
            currentString = currentText.getText().toString();
            preferences.put("pet_peeves", currentString);

            //allergies
            currentText = (TextView)findViewById(R.id.input_allergies);
            currentString = currentText.getText().toString();
            preferences.put("allergies", currentString);

            //photo_url
            preferences.put("photo_url", url);



            //uploading...
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference().child("users").child(extras.getString("username"));
            ref.updateChildren(preferences);

            /** Go to homepage **/
            Intent goToHomePage = new Intent(this, NavDrawerActivity.class);
            goToHomePage.putExtra("username", extras.getString("username"));
            startActivity(goToHomePage);
            finish();
        }
        else{
            //do nothing
        }

    }

    /**
     * Validates the inputs in the questionare
     * @return true if everything is inputed correctly
     */
    private Boolean validate(View view){
        TextView currentView;
        String currentText;
        //check emergency contact name
        currentView = (TextView)findViewById(R.id.input_emergency_name);
        currentText = currentView.getText().toString();
        if(currentText.equals("")){
            createToast("Emergency contact name cannot be empty");
            return false;
        }


        //input_emergency_relationship
        currentView = (TextView)findViewById(R.id.input_emergency_relationship);
        currentText = currentView.getText().toString();
        if(currentText.equals("")){
            createToast("Emergency contact relationship cannot be empty");
            return false;
        }

        //input_emergency_phone
        currentView = (TextView)findViewById(R.id.input_emergency_phone);
        currentText = currentView.getText().toString();
        if(currentText.equals("")){
            createToast("Emergency contact phone number cannot be missing");
            return false;
        }else if(currentText.length() > 10) {
            createToast("Format phone number as digits only");
            return false;
        }else if(currentText.length() < 10){
            createToast("Phone number incomplete");
            return false;
        }

        //input_about_me
        currentView = (TextView)findViewById(R.id.input_about_me);
        currentText = currentView.getText().toString();
        if(currentText.equals("")){
            createToast("About me cannot be empty");
            return false;
        }

        //input_pet_peeve
        currentView = (TextView)findViewById(R.id.input_pet_peeve);
        currentText = currentView.getText().toString();
        if(currentText.equals("")){
            createToast("Pet peeves cannot be empty");
            return false;
        }

        //input_allergies
        currentView = (TextView)findViewById(R.id.input_allergies);
        currentText = currentView.getText().toString();
        if(currentText.equals("")){
            createToast("Allergies cannot be empty");
            return false;
        }

        if(url.equals("")){
            url = "https://firebasestorage.googleapis.com/v0/b/liveasy-85049.appspot.com/o/woodie.jpg?alt=media&token=87057c2f-1d19-4b23-90f1-1f215b5ad618";
            return true;
        }


        return true;
    }

    /**
     * Will create a toast with specified string
     * @param message string to be displayed in the toast
     */
    private void createToast(String message){
        Toast toast = Toast.makeText(this, message,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }


    /** Change status of contact **/
    public void toggleContact(View view){
        closeAll("contact");
        LinearLayout contactLayout = (LinearLayout)findViewById(R.id.contact_layout);
        if(contactIsOpen){
            TextView eContactText = (TextView)findViewById(R.id.eContactText);
            eContactText.setText("+ Emergency Contact Info");
            contactLayout.setVisibility(LinearLayout.GONE);
            contactIsOpen = false;
        }
        else{
            TextView eContactText = (TextView)findViewById(R.id.eContactText);
            eContactText.setText("- Emergency Contact Info");

            contactLayout.setVisibility(LinearLayout.VISIBLE);
            contactIsOpen = true;
        }
    }

    /** change status of about me **/
    public void toggleAboutMe(View view){
        closeAll("about_me");
        LinearLayout aboutMeLayout = (LinearLayout)findViewById(R.id.about_me_layout);
        if(aboutMeIsOpen){
            TextView aboutMeText = (TextView)findViewById(R.id.about_me_text);
            aboutMeText.setText("+ About Me");
            aboutMeLayout.setVisibility(LinearLayout.GONE);
            aboutMeIsOpen = false;
        }
        else{
            TextView aboutMeText = (TextView)findViewById(R.id.about_me_text);
            aboutMeText.setText("- About Me");

            aboutMeLayout.setVisibility(LinearLayout.VISIBLE);
            aboutMeIsOpen = true;
        }
    }

    /** change status of preferences **/
    public void togglePreferences(View view){
        closeAll("preferences");
        RelativeLayout preferencesLayout = (RelativeLayout) findViewById(R.id.questionnaire_checkboxes);
        if(preferenceIsOpen){
            TextView preferencesText = (TextView)findViewById(R.id.preferences_text);
            preferencesText.setText("+ Preferences");
            preferencesLayout.setVisibility(LinearLayout.GONE);
            preferenceIsOpen = false;
        }
        else{
            TextView preferencesText = (TextView)findViewById(R.id.preferences_text);
            preferencesText.setText("- Preferences");

            preferencesLayout.setVisibility(LinearLayout.VISIBLE);
            preferenceIsOpen = true;
        }
    }

    /** change status of pet peeves **/
    public void togglePetPeeves(View view){
        closeAll("pet_peeves");
        LinearLayout petPeevesLayout = (LinearLayout) findViewById(R.id.pet_peeve_layout);
        if(petPeevesIsOpen){
            TextView petPeevesText = (TextView)findViewById(R.id.pet_peeves_text);
            petPeevesText.setText("+ Pet Peeves");
            petPeevesLayout.setVisibility(LinearLayout.GONE);
            petPeevesIsOpen = false;
        }
        else{
            TextView petPeevesText = (TextView)findViewById(R.id.pet_peeves_text);
            petPeevesText.setText("- Pet Peeves");

            petPeevesLayout.setVisibility(LinearLayout.VISIBLE);
            petPeevesIsOpen = true;
        }
    }

    /** change status of allergies **/
    public void toggleAllergies(View view){
        closeAll("allergies");
        LinearLayout allergiesLayout = (LinearLayout) findViewById(R.id.allergies_layout);
        if(allergiesIsOpen){
            TextView allergiesText = (TextView)findViewById(R.id.allergies_text);
            allergiesText.setText("+ Allergies");
            allergiesLayout.setVisibility(LinearLayout.GONE);
            allergiesIsOpen = false;
        }
        else{
            TextView allergiesText = (TextView)findViewById(R.id.allergies_text);
            allergiesText.setText("- Allergies");

            allergiesLayout.setVisibility(LinearLayout.VISIBLE);
            allergiesIsOpen = true;
        }
    }


    /**
     * Collapse all layouts except for the exception
     * @param exception which category will not be collapsed
     */
    public void closeAll(String exception){
        if(!exception.equals("contact")) {
            LinearLayout contactLayout = (LinearLayout) findViewById(R.id.contact_layout);
            TextView eContactText = (TextView) findViewById(R.id.eContactText);
            eContactText.setText("+ Emergency Contact Info");
            contactLayout.setVisibility(LinearLayout.GONE);
            contactIsOpen = false;
        }

        if(!exception.equals("about_me")) {
            LinearLayout aboutMeLayout = (LinearLayout) findViewById(R.id.about_me_layout);
            TextView aboutMeText = (TextView) findViewById(R.id.about_me_text);
            aboutMeText.setText("+ About Me");
            aboutMeLayout.setVisibility(LinearLayout.GONE);
            aboutMeIsOpen = false;
        }

        if(!exception.equals("preferences")) {
            RelativeLayout preferencesLayout = (RelativeLayout) findViewById(R.id.questionnaire_checkboxes);
            TextView preferencesText = (TextView) findViewById(R.id.preferences_text);
            preferencesText.setText("+ Preferences");
            preferencesLayout.setVisibility(LinearLayout.GONE);
            preferenceIsOpen = false;
        }
        if(!exception.equals("pet_peeves")) {

            LinearLayout petPeevesLayout = (LinearLayout) findViewById(R.id.pet_peeve_layout);
            TextView petPeevesText = (TextView) findViewById(R.id.pet_peeves_text);
            petPeevesText.setText("+ Pet Peeves");
            petPeevesLayout.setVisibility(LinearLayout.GONE);
            petPeevesIsOpen = false;
        }

        if(!exception.equals("allergies")) {
            LinearLayout allergiesLayout = (LinearLayout) findViewById(R.id.allergies_layout);
            TextView allergiesText = (TextView) findViewById(R.id.allergies_text);
            allergiesText.setText("+ Allergies");
            allergiesLayout.setVisibility(LinearLayout.GONE);
            allergiesIsOpen = false;
        }

    }

    // Picture Taking functionality
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                System.out.println("photoFile was not null*****");
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

        Log.i(TAG, "onActivityResult: " + this);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {

            Bitmap bitmap = setPic();
            //Set up database uploading here


            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
            Uri file = Uri.fromFile(new File(mCurrentPhotoPath));
            StorageReference profileRef = mStorageRef.child(extras.getString("username"));




            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 33, baos);
            byte[] fileData = baos.toByteArray();
            UploadTask uploadTask = profileRef.putBytes(fileData);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    System.out.println("Upload unsuccessful");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    url = downloadUrl.toString();
                    System.out.println("Upload successful");
                }
            });

        }
    }

    private Bitmap setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor << 1;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        if(bitmap == null)
        {
            System.out.println("bitmap = null...");
        }


        ExifInterface exif = null;
        try {
            exif = new ExifInterface(mCurrentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap bmRotated = rotateBitmap(bitmap, orientation);

        mImageView.setImageBitmap(bmRotated);

        return bmRotated;
    }


    private void cameraPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            System.out.println("At cameraPermission***");

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.CAMERA)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                System.out.println("Inside if Camera Permission*******");

            } else {

                // No explanation needed, we can request the permission.
                System.out.println("Inside else Camera Permission*******");


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
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ);

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
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

