package com.example.joker.emojifyme;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_STORAGE_PERMISSION = 001;
    private static final int CAMERA_REQUEST = 100;
    private Button emojifyButton;
    private TextView titleTextView;
    private ImageView imageView;
    private FloatingActionButton clear, save, share;

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

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("pivot","in onActivityResult");
        if (requestCode == CAMERA_REQUEST && resultCode==Activity.RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            if (bitmap != null) {
                Log.d("pivot"," setting bitmap");
                setImage(bitmap);
            }
        }
    }

    private void setImage(Bitmap bitmap) {

        titleTextView.setVisibility(View.GONE);
        emojifyButton.setVisibility(View.GONE);
        imageView.setImageBitmap(bitmap);
        save.setVisibility(View.VISIBLE);
        share.setVisibility(View.VISIBLE);
        clear.setVisibility(View.VISIBLE);

    }
}
