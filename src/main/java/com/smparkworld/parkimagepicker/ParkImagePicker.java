package com.smparkworld.parkimagepicker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;

import static androidx.core.content.ContextCompat.checkSelfPermission;

public class ParkImagePicker extends BottomSheetDialog {

    public static final int PMSS_READ_EXT_STORAGE = 5000;
    public static final int PMSS_WRITE_EXT_STORAGE = 5001;
    public static final int PMSS_CAMERA = 5002;

    private Context context;
    private int mNumOfColumns = 3;
    public static OnImageSelectedListener mListener;
    public static ImageView mImageView;

    public ParkImagePicker(Context context) {
        super(context);
        setContentView(R.layout.dialog_park_image_picker);

        this.context = context;
    }

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mNumOfColumns <= 0) mNumOfColumns = 1;

        loadImageFromDevice((RecyclerView)findViewById(R.id.rvContainer));
        findViewById(R.id.btnTaskPicture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ParkImagePickerTakePictureActivity.class);
                context.startActivity(i);
                dismiss();
            }
        });
    }

    private boolean loadImageFromDevice(RecyclerView rvContainer) {
        if (rvContainer == null) {
            Log.v("ParkImagePicker error!", "The Container return null.");
            return false;
        }

        if (!requestPermission()) {
            Log.v("ParkImagePicker error!", "Permission denied: READ_EXTERNAL_STORAGE or WRITE_EXTERNAL_STORAGE or CAMERA");
            return false;
        }

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"
        );

        // Check if the cursor is invalid.
        if (cursor == null || !cursor.moveToFirst()) {
            Log.v("ParkImagePicker error!", "Failed to create cursor.");
            return false;
        }

        // Generate modelList.
        ArrayList<String> modelList = new ArrayList<String>();

        int fieldIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        while (cursor.moveToNext())
            modelList.add(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getString(fieldIndex)).toString());

        rvContainer.setLayoutManager(new GridLayoutManager(context, mNumOfColumns));
        rvContainer.setAdapter(new ParkImagePickerAdapter(context, this, modelList));
        return true;
    }

    private boolean requestPermission() {
        boolean ret = true;
        // android.permission.READ_EXTERNAL_STORAGE
        if (checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(context, R.string.requestImageAccessPermission, Toast.LENGTH_SHORT).show();
                ret = false;
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions((Activity)context, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PMSS_READ_EXT_STORAGE);
            }
        }
        // android.permission.WRITE_EXTERNAL_STORAGE
        if (checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(context, R.string.requestImageAccessPermission, Toast.LENGTH_SHORT).show();
                ret = false;
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions((Activity)context, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, PMSS_WRITE_EXT_STORAGE);
            }
        }
        // android.permission.CAMERA
        if (checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)context, Manifest.permission.CAMERA)) {
                Toast.makeText(context, R.string.requestImageAccessPermission, Toast.LENGTH_SHORT).show();
                ret = false;
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions((Activity)context, new String[] {Manifest.permission.CAMERA}, PMSS_CAMERA);
            }
        }
        return ret;
    }

    public static void clearCache(Context context) {
        File cache = context.getExternalCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                deleteDir(new File(appDir, s));
            }
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                Log.d("ParkImagePicker result!", "File: " + children[i] + " DELETED");
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // option methods../////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public interface OnImageSelectedListener {
        void onImageSelected(String uri);
    }

    public ParkImagePicker setOnSelectedListener(OnImageSelectedListener listener) {
        mListener = listener;
        return this;
    }

    public ParkImagePicker setOnSelectedImageView(ImageView iv) {
        mImageView = iv;
        return this;
    }

    public ParkImagePicker setTakePictureBtn(boolean isShow) {
        if (isShow)
            findViewById(R.id.btnTaskPicture).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.btnTaskPicture).setVisibility(View.GONE);

        return this;
    }

    public ParkImagePicker setNumOfColumns(int column) {
        mNumOfColumns = column;
        return this;
    }

    public ParkImagePicker setTitleBackgroundColor(int colorId) {
        findViewById(R.id.llTitleBack).setBackgroundColor(context.getResources().getColor(colorId));
        return this;
    }

    public ParkImagePicker setTitleFontColor(int colorId) {
        ((TextView)findViewById(R.id.tvTitle)).setTextColor(context.getResources().getColor(colorId));
        return this;
    }

    public ParkImagePicker setTitle(String title) {
        ((TextView)findViewById(R.id.tvTitle)).setText(title);
        return this;
    }

    public ParkImagePicker setTaskPictureBtnIcon(int drawableId) {
        ((ImageButton)findViewById(R.id.btnTaskPicture)).setImageDrawable(context.getResources().getDrawable(drawableId));
        return this;
    }

    public void show() {
        super.show();
    }

}