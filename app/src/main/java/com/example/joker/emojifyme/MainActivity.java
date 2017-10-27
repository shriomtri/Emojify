package com.example.joker.emojifyme;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_STORAGE_PERMISSION = 001;
    private static final int CAMERA_REQUEST = 100;
    private static final String FILE_PROVIDER_AUTORITY = "com.example.android.fileprovider";
    private Button emojifyButton;
    private TextView titleTextView;
    private ImageView imageView;
    private FloatingActionButton clear, save, share;
    private String tempPhotPath;
    private Bitmap resultBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emojifyButton = (Button) findViewById(R.id.emojify_button);
        titleTextView = (TextView) findViewById(R.id.title_textView);
        imageView = (ImageView) findViewById(R.id.image_view);
        clear = (FloatingActionButton) findViewById(R.id.btn_clear);
        save = (FloatingActionButton) findViewById(R.id.btn_save);
        share = (FloatingActionButton) findViewById(R.id.btn_share);


    }

    //on emojify Btn clicked
    public void emojifyMe(View view) {
        //check for the permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {

            //if you dont have permission request for it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);

        } else {
            //launch the camera if exists
            launchCamera();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //If you get permission launch camera
                    launchCamera();
                } else {
                    //if did'nt get the permission show the Toast.
                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    private void launchCamera() {

        //create a camera intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Ensure there is a camera activity to handle the intent
        if(takePictureIntent.resolveActivity(getPackageManager())!=null){

            //create a tempe file File where file should go
            File photoFile = null;
            try{
                photoFile = BitmapUtils.createTempImageFile(this);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(photoFile!=null){

                //get the path of the temporary file
                tempPhotPath = photoFile.getAbsolutePath();

                //get the content URI for the image file
                Uri photoURI = FileProvider.getUriForFile(
                        this,
                        FILE_PROVIDER_AUTORITY,
                        photoFile
                );

                //add the uri so the camera can store the image
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);

                //launch the camera activity
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);


            }

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("pivot","in onActivityResult");
        if (requestCode == CAMERA_REQUEST && resultCode==Activity.RESULT_OK) {
            processAndSetImage();
        }else{
            BitmapUtils.deletImage(this,tempPhotPath);
        }
    }

    private void processAndSetImage() {

        titleTextView.setVisibility(View.GONE);
        emojifyButton.setVisibility(View.GONE);
        save.setVisibility(View.VISIBLE);
        clear.setVisibility(View.VISIBLE);
        share.setVisibility(View.VISIBLE);

        resultBitmap = BitmapUtils.rescaleBitmap(MainActivity.this,tempPhotPath);
        imageView.setImageBitmap(resultBitmap);

    }


    //on save btn clicked
    public void saveMe(View view) {
        new AsyncSave().execute();
    }

    //on share btn clicked
    public void shareMe(View view) {
        new AsyncShare().execute();
    }

    //on clear btn clicked
    public void clear(View view) {

         new AsyncClear().execute();

    }

    //async Share
    private class AsyncShare extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {

            BitmapUtils.deletImage(MainActivity.this,tempPhotPath);

            String imagePath = BitmapUtils.saveImage(MainActivity.this,resultBitmap);

            BitmapUtils.shareImage(MainActivity.this,imagePath);

            return null;
        }
    }


    //async Save
    private class AsyncSave extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {

            //delete temp file
            BitmapUtils.deletImage(MainActivity.this,tempPhotPath);

            //save the file as permanent
            BitmapUtils.saveImage(MainActivity.this,resultBitmap);

            return null;
        }
    }

    //async Clear
    private class AsyncClear extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {

            BitmapUtils.deletImage(MainActivity.this,tempPhotPath);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            titleTextView.setVisibility(View.VISIBLE);
            emojifyButton.setVisibility(View.VISIBLE);
            save.setVisibility(View.GONE);
            clear.setVisibility(View.GONE);
            share.setVisibility(View.GONE);
            imageView.setImageResource(0);

        }
    }
}
