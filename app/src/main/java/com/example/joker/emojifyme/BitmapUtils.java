package com.example.joker.emojifyme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by joker on 27/10/17.
 */

public class BitmapUtils {

    private static final String FILE_PROVIDER_AUTHORITY = "com.example.android.fileprovider";

    //create temp cache file in temp derictory
    public static File createTempImageFile(Context context) throws IOException {

//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
//                Locale.getDefault()).format(new Date());

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalCacheDir();

        return File.createTempFile(fileName, ".jpg", storageDir);
    }

    public static void deletImage(Context context, String tempPhotPath) {

        File imageFile = new File(tempPhotPath);

        boolean delete = imageFile.delete();

        if (!delete) {
            String errorMessage = "Error deleting Image";
            //Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            Log.d("error ",errorMessage);
        }
    }

    public static Bitmap rescaleBitmap(Context context, String imagePath) {


        //get device screen size information
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metrics);

        int targetH = metrics.heightPixels;
        int targetW = metrics.widthPixels;

        //get dimension of the orignal bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        //determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        //decode the image file into a bitmap size to fill the view
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeFile(imagePath);

    }

    public static String saveImage(Context context, Bitmap resultBitmap) {

        String saveImagePath = null;

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";

        File storageDir =  new File(Environment.getExternalStorageDirectory()
                + "/Emojify");
        boolean success = true;

        if(!storageDir.exists()){
            success = storageDir.mkdirs();
        }

        //save the new bitmap

//        // Save the new Bitmap
//        if (success) {
//            File imageFile = new File(storageDir, imageFileName);
//            savedImagePath = imageFile.getAbsolutePath();
//            try {
//                OutputStream fOut = new FileOutputStream(imageFile);
//                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
//                fOut.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            // Add the image to the system gallery
//            galleryAddPic(context, savedImagePath);
//
//            // Show a Toast with the save location
//            String savedMessage = context.getString(R.string.saved_message, savedImagePath);
//            Toast.makeText(context, savedMessage, Toast.LENGTH_SHORT).show();
//        }
//

        if(success){
            File imageFile = new File(storageDir,imageFileName);
            saveImagePath = imageFile.getAbsolutePath();
            try{
                OutputStream fout = new FileOutputStream(imageFile);
                resultBitmap.compress(Bitmap.CompressFormat.JPEG,100,fout);
                fout.close();
            }catch (Exception e){
                e.printStackTrace();
            }

            //add the image to the system gallery
            galleryAddPic(context,saveImagePath);

            //show a toast with the save location
            String savedMessage = saveImagePath;

            //Toast.makeText(context , savedMessage , Toast.LENGTH_SHORT).show();
            Log.d("location ",savedMessage);
        }

        return saveImagePath;
    }

    private static void galleryAddPic(Context context, String saveImagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(saveImagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);

    }


    static void shareImage(Context context, String imagePath) {
        // Create the share intent and start the share activity

        Log.d("image path",imagePath);

        File imageFile = new File(imagePath);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        Uri photoURI = Uri.parse(imageFile.toString());
        shareIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(shareIntent);
    }

}
