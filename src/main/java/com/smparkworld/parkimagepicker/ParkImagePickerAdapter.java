package com.smparkworld.parkimagepicker;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ParkImagePickerAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<String> modelList;
    private Dialog dialog;
    private ParkImagePicker.OnImageSelectedListener mListener;
    private ImageView mImageView;

    public ParkImagePickerAdapter(Context context, Dialog dialog, ArrayList<String> modelList) {
        this.context = context;
        this.modelList = modelList;
        this.dialog = dialog;
        this.mListener = ParkImagePicker.mListener;
        this.mImageView = ParkImagePicker.mImageView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        SquareImageView iv = new SquareImageView(context);

        // Set margins..
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(2, 2, 2, 2);
        iv.setLayoutParams(params);

        return new ViewHolder(iv);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        ImageView ivImage = ((ViewHolder)holder).imageView;

        final String uri = modelList.get(position);
        ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) mListener.onImageSelected(uri);
                if (mImageView != null) mImageView.setImageURI(Uri.parse(uri));
                dialog.dismiss();
            }
        });

        Glide.with(context).load(uri).into(ivImage);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView)v;
        }
    }

    private class SquareImageView extends AppCompatImageView {

        public SquareImageView(Context context) {
            super(context);
        }

        public SquareImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            // Set squared.
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        }

    }

}