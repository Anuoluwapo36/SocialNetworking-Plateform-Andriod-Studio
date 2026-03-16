package com.example.ziniraproject.until;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.ziniraproject.R;
import com.google.android.material.imageview.ShapeableImageView;


public class ImageLoader {
    private  static  final int DEFAULT_IMAGE= R.drawable.default_user_gray;
    private Context context;


    public ImageLoader(Context context) {
        this.context = context;
    }

    public static void loadImage(String url, ImageView imageView, ProgressBar mprogressBar) {
        Context context = imageView.getContext();

        // Check if context is valid and not destroyed
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return; // Don't load image into a destroyed activity
            }
        }

        if (mprogressBar != null) {
            mprogressBar.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }

        Glide.with(context)
                .load(url)
                .placeholder(DEFAULT_IMAGE)
                .error(DEFAULT_IMAGE)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                        if (mprogressBar != null) {
                            mprogressBar.setVisibility(View.GONE);
                            imageView.setVisibility(View.VISIBLE);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, @NonNull Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                        if (mprogressBar != null) {
                            mprogressBar.setVisibility(View.GONE);
                            imageView.setVisibility(View.VISIBLE);
                        }
                        return false;
                    }
                })
                .into(imageView);
    }


//    /**
//     * special handling for shapableImageView
//     */
//    public  static  void  loadIntoShapeAble(String url, ShapeableImageView imageView, ProgressBar mprogressBar){
//        loadImage(url,imageView,mprogressBar);
//
//    }


}
