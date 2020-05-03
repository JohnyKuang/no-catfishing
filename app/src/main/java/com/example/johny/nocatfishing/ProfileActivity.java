package com.example.johny.nocatfishing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.johny.nocatfishing.Profile.CustomImageSlideAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ProfileActivity extends AppCompatActivity {
    private ArrayList<String> al;
    private ArrayAdapter<String> arrayAdapter;
    private int i;
    final int REQUEST_IMAGE_CAPTURE = 1;
    public ImageView mImageView;
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;

    private boolean editPermission = true;
    //private int[] image_resources = {R.drawable.ocean, R.drawable.cf};
    ArrayList<Bitmap> image_resources = new ArrayList<Bitmap>();

    private FirebaseAuth mAuth;
    private String userId;
    private DatabaseReference userReference;
    private String aboutMeContent;
    private boolean editButtonClicked = false;

    private Uri resultUri;
    private Bitmap resultBitmap; //these 2 result variables are for saving images to firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mImageView = (ImageView)findViewById(R.id.temp_photo);
        aboutMeContent = "this is my temporary default description here";
        checkPermission();
        //all for view pager

        //setImage_resources();
        ViewPager myViewPager = (ViewPager)findViewById(R.id.view_pager);
        CustomImageSlideAdapter myAdapter = new CustomImageSlideAdapter(this,image_resources);
        myViewPager.setAdapter(myAdapter);

        //reference the current user and database
        String userSex = getIntent().getExtras().getString("userSex"); //getting a variable from Home activity
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(userId);

        //to get info to from db to display
        getUserInfo();

    }

    //runs when submit button is clicked
    public void saveUserInformation(View view){
        Map userInfo = new HashMap();
        if (editButtonClicked == true){
            EditText myEditText = (EditText)findViewById(R.id.about_me_content_editable);
            aboutMeContent = myEditText.getText().toString();
            userInfo.put("aboutMe", aboutMeContent);
        }
        ////////////////////gotta save photos right here
        if (resultBitmap != null){
            //create a name and file path for the image to be saved
            SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
            String format = s.format(new Date());
            String imageID = userId + format;
            final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profileImages").child(imageID);
            //now to compress the bitmap image
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resultBitmap.compress(Bitmap.CompressFormat.JPEG,20, baos);
            byte []data = baos.toByteArray();
            //upload data
            UploadTask uploadTask = filepath.putBytes(data);
            //the following link was used to code lines below which was to download url of photo after it had been uploaded to storage
            //https://stackoverflow.com/questions/50467814/tasksnapshot-getdownloadurl-is-deprecated/50468622?fbclid=IwAR201eL37GQEbJp8KYlz6Tf2XRNiVbNvHq7QyEuGt1n4722RPWbkN0RaC7c
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String downloadURL = downloadUri.toString();
                        Map userInfo = new HashMap();
                        userInfo.put("profileImageUrl", downloadURL);
                        userReference.updateChildren(userInfo);
                        Toast.makeText(ProfileActivity.this, "Photo Saved",Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    } else {
                        Toast.makeText(ProfileActivity.this, "ERROR",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        ///////////////////////////////////////////////////////////////////////
        userReference.updateChildren(userInfo);
        Toast.makeText(ProfileActivity.this, "Info Saved",Toast.LENGTH_SHORT).show();
    }

    //for displaying info from database
    public void getUserInfo(){
        userReference.addListenerForSingleValueEvent(new ValueEventListener() { //single because we only check once
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){ //if the reference exists and has children
                    Map<String, Object> map= (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("aboutMe") != null){//if something exists there
                        aboutMeContent = map.get("aboutMe").toString();
                        TextView aboutMeContentTextView = (TextView) findViewById(R.id.about_me_content);
                        aboutMeContentTextView.setText(aboutMeContent);
                    }
                    if (map.get("profileImageUrl") != null){//if user has images
                        final String profString = "" + map.get("profileImageUrl");
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //URL url = new URL("https://firebasestorage.googleapis.com/v0/b/nocatfishing-f1ceb.appspot.com/o/profileImages%2FLlzYbaMhBdOK5LxFH1tLGgApadw209022019080533?alt=media&token=07b0c774-fc67-483a-9733-a82d79fb2299");
                                    URL url = new URL(profString);
                                    Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                    image_resources.add(image);
                                } catch(IOException e) {
                                    System.out.println(e);
                                }
                            }
                        });
                        thread.start();
                        try { //to wait for the thread to finish before continuing
                            thread.join();
                            displayImageSlider(); //display image once thread is done
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //runs when Back To Swiping button is clicked
    public void backToSwipingButton(View view){
        Intent intent = new Intent(this,Home.class);
        startActivity(intent);
    }

    //checks to see if the person on the profile is allowed to edit
    public void checkPermission(){

        if (editPermission == true){
            //edit text button is visible and... aboutMeContentTextView.setText(test);
            Button editBtn = (Button)findViewById(R.id.edit_btn);
            editBtn.setVisibility(View.VISIBLE);
            TextView aboutMeContentTextView = (TextView) findViewById(R.id.about_me_content);
            aboutMeContentTextView.setText(aboutMeContent);
            aboutMeContentTextView.setVisibility(View.VISIBLE);
        } else{
            //set textview to other user's content
            TextView aboutMeContentTextView = (TextView)findViewById(R.id.about_me_content);
            aboutMeContentTextView.setText(aboutMeContent);
            aboutMeContentTextView.setVisibility(View.VISIBLE);

        }
    }

    //runs when edit button is clicked
    public void editText(View view){
        //first make textView and edit button invisible
        TextView aboutMeContentTextView = (TextView) findViewById(R.id.about_me_content);
        aboutMeContentTextView.setVisibility(View.INVISIBLE);
        Button editBtn = (Button)findViewById(R.id.edit_btn);
        editBtn.setVisibility(View.INVISIBLE);
        //now make done_btn visible
        Button doneBtn = (Button)findViewById(R.id.done_btn);
        doneBtn.setVisibility(View.VISIBLE);
        //put the current content(description) into edittext
        EditText myEditText = (EditText)findViewById(R.id.about_me_content_editable);
        myEditText.setText(aboutMeContent);
        myEditText.setVisibility(View.VISIBLE);
        editButtonClicked = true;
    }

    //runs when done button is clicked
    public void doneBtn(View view){
        EditText myEditText = (EditText)findViewById(R.id.about_me_content_editable);
        aboutMeContent = myEditText.getText().toString();
        //now make done btn disappear and set the textview to new description
        Button doneBtn = (Button)findViewById(R.id.done_btn);
        doneBtn.setVisibility(View.INVISIBLE);
        //textview to new description
        myEditText.setVisibility(View.INVISIBLE);
        TextView myTextView = (TextView) findViewById(R.id.about_me_content);
        myTextView.setText(aboutMeContent);
        myTextView.setVisibility(View.VISIBLE);
        //edit button should appear again
        Button myEditBtn = (Button)findViewById(R.id.edit_btn);
        myEditBtn.setVisibility(View.VISIBLE);
    }

    public void takePicture(View view){
        dispatchTakePictureIntent();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                Bitmap mImageBitmap;
                //store image in array then display first image in array
                mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                /////////////we can save the imageBitmap to DB here
                final Uri imageUri = data.getData();
                resultUri = imageUri;
                resultBitmap = mImageBitmap;
                /////////////////////////////////////////////////////////////////////////////
                image_resources.add(mImageBitmap); //where we add to the bitmap array
                displayImageSlider();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File storageDir = res/drawable/;
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath(); //prepending "file:" so image displays in activity
        return image;
    }
    private void dispatchTakePictureIntent() {  //saving of the captured photo
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //implicit intent: need something from another app
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
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO); //pulls up the camera
            }
        }
    }
    public void displayImageSlider(){
        ViewPager myViewPager = (ViewPager)findViewById(R.id.view_pager);
        CustomImageSlideAdapter myAdapter = new CustomImageSlideAdapter(this,image_resources);
        myViewPager.setAdapter(myAdapter);
    }

}