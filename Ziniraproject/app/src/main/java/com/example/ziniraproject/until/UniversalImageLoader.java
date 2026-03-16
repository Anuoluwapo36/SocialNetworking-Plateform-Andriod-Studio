//package com.example.ziniraproject.until;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//
//import com.example.ziniraproject.R;
//import com.google.android.material.imageview.ShapeableImageView;
//import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
//import com.nostra13.universalimageloader.core.DisplayImageOptions;
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
//import com.nostra13.universalimageloader.core.assist.FailReason;
//import com.nostra13.universalimageloader.core.assist.ImageScaleType;
//import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
//import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
//
//public class UniversalImageLoader {
//    private static final int defaultImage = R.drawable.user;
//    private final Context context;
//    private static boolean isInitialized = false;
//
//    public UniversalImageLoader(Context context) {
//        this.context = context;
//        // Initialize ImageLoader if not already done
//        if (!isInitialized) {
//            ImageLoader.getInstance().init(getConfig());
//            isInitialized = true;
//        }
//    }
//
//    public ImageLoaderConfiguration getConfig() {
//        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
//                .showImageOnLoading(defaultImage)
//                .showImageForEmptyUri(defaultImage)
//                .showImageOnFail(defaultImage)
//                .cacheOnDisk(true)
//                .cacheInMemory(true)
//                .resetViewBeforeLoading(true)
//                .imageScaleType(ImageScaleType.EXACTLY)
//                .displayer(new FadeInBitmapDisplayer(300))
//                .build();
//
//        return new ImageLoaderConfiguration.Builder(context)
//                .defaultDisplayImageOptions(displayImageOptions)
//                .memoryCache(new WeakMemoryCache())
//                .diskCacheSize(100 * 1024 * 1024)
//                .build();
//    }
//
//    /**
//     * Universal image loader for both regular ImageView and ShapeableImageView
//     *
//     * @param imgURL image URL to load
//     * @param image ImageView or ShapeableImageView to load into
//     * @param progressBar optional progress bar (can be null)
//     * @param append optional string to prepend to URL (can be null or empty)
//     */
//    public static void setImage(String imgURL, ImageView image, final ProgressBar progressBar, String append) {
//        String fullUrl = (append != null) ? append + imgURL : imgURL;
//
//        ImageLoader.getInstance().displayImage(fullUrl, image, new ImageLoadingListener() {
//            @Override
//            public void onLoadingStarted(String imageUri, View view) {
//                if (progressBar != null) {
//                    progressBar.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                if (progressBar != null) {
//                    progressBar.setVisibility(View.GONE);
//                }
//                if (view instanceof ShapeableImageView) {
//                    ((ShapeableImageView) view).setImageResource(defaultImage);
//                } else {
//                    ((ImageView) view).setImageResource(defaultImage);
//                }
//            }
//
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                if (progressBar != null) {
//                    progressBar.setVisibility(View.GONE);
//                }
//                // For ShapeableImageView, we need to explicitly set the bitmap
//                if (view instanceof ShapeableImageView) {
//                    ((ShapeableImageView) view).setImageBitmap(loadedImage);
//                }
//                // Regular ImageView will handle it automatically
//            }
//
//            @Override
//            public void onLoadingCancelled(String imageUri, View view) {
//                if (progressBar != null) {
//                    progressBar.setVisibility(View.GONE);
//                }
//            }
//        });
//    }
//}