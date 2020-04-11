package com.smparkworld.parkimagepicker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ParkImagePickerTakePictureActivity extends AppCompatActivity {

    public static final int REQUEST_CAMERA = 100;

    public String mImagePathForActionView;

    private ParkImagePicker.OnImageSelectedListener mListener;
    private ImageView mImageView;
    private Uri mTargetUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListener = ParkImagePicker.mListener;
        mImageView = ParkImagePicker.mImageView;

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (i.resolveActivity(getPackageManager()) == null) {
            Log.v("ParkImagePicker error!", "resolveActivity method return null.");
            return;
        }

        try {
            File tempFile = createTempImage();
            if (tempFile == null) throw new IOException();

            String storageState = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(storageState)) {
                mTargetUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", tempFile);
            } else {
                Log.v("ParkImagePicker error!", "SD card cannot be used");
                return;
            }

            i.putExtra(MediaStore.EXTRA_OUTPUT, mTargetUri);
            startActivityForResult(i, REQUEST_CAMERA);

        } catch (IOException e) {
            Log.v("ParkImagePicker error!", "Failed to create temp image file.");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA) {
            if (mListener != null) mListener.onImageSelected(mImagePathForActionView);
            if (mImageView != null) mImageView.setImageURI(Uri.parse(mImagePathForActionView));
        }
        finish();
    }

    private File createTempImage() throws IOException {
        // Create an image file name
        String imageFileName = new SimpleDateFormat("yyyyMMdd_Hms").format(new Date());
        File storageDir = getExternalCacheDir();

        File tempFile = File.createTempFile(
                imageFileName,    // prefix
                ".jpg",     // suffix
                storageDir        // storage directory
        );

        // Path for use with ACTION_VIEW intent.
        mImagePathForActionView = tempFile.getAbsolutePath();
        return tempFile;
    }
}
