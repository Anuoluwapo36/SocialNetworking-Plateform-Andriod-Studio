package com.example.ziniraproject.until;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.ziniraproject.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class GridViewAdapter extends ArrayAdapter<String> {

    private Context context;
    private LayoutInflater minflater;
    private  int LayoutResource;
    private  String mAppend;

    private  static  final int DEFAULT_IMAGE= R.drawable.z_logo_foreground;
    private ArrayList<String> imgUrl;

    public GridViewAdapter(@NonNull Context context,   int layoutResource, String mAppend, ArrayList<String> imgUrl) {
        super(context, layoutResource);
        this.minflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;

        LayoutResource = layoutResource;
        this.mAppend = mAppend;
        this.imgUrl = imgUrl;
    }

    private  static  class  ViewHolder{
        SquareImageView profileImage;
        ProgressBar mprogressBar;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View  convertView , @NonNull ViewGroup parent){

        /*
         * viewHolder Build pattern( i will update to recyclerview later )
         */
        final  ViewHolder viewHolder;

        if (convertView == null){
            convertView=minflater.inflate(LayoutResource,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.mprogressBar = (ProgressBar)  convertView.findViewById(R.id.gridProgressbar);
            viewHolder.profileImage=(SquareImageView) convertView.findViewById(R.id.gridImageView);

            convertView.setTag(viewHolder);

        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String imgUrl = getItem(position);
        loadImage(mAppend + imgUrl, viewHolder.profileImage, viewHolder
                .mprogressBar);
        return convertView ;
    }
    private void loadImage(String url, ImageView imageView, ProgressBar progressBar) {
        if (progressBar != null){
            progressBar.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        }
//        RequestOptions requestOptions = new RequestOptions()
//                .override(100,100)
//                        .centerInside();

        Glide.with(context)
                .load(url)
//                .apply(requestOptions)
                .placeholder(DEFAULT_IMAGE)
//                .error(Glide.with(context).load(DEFAULT_IMAGE).apply(requestOptions))
                .error(DEFAULT_IMAGE)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
//                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        if (progressBar != null){
                            progressBar.setVisibility(View.GONE);
                            imageView.setVisibility(View.VISIBLE);
                        }

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
//                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        if (progressBar != null){
                            progressBar.setVisibility(View.GONE);
                            imageView.setVisibility(View.VISIBLE);
                        }

                        return false;
                    }
                })
                .into(imageView);
    }

    @Override
    public int getCount() {
        return imgUrl.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return imgUrl.get(position);
    }
}
