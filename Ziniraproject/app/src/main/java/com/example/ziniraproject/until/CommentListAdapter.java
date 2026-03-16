package com.example.ziniraproject.until;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ziniraproject.R;
import com.example.ziniraproject.models.Comment;
import com.example.ziniraproject.models.UserAccountSettings;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CommentListAdapter  extends ArrayAdapter<Comment> {




    private static final String TAG = "CommentListAdapter";

    private LayoutInflater mInflater;
    private  int layoutResource;
    private  Context mContext;



    public CommentListAdapter(@NonNull Context context, int resource, @NonNull List<Comment> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater)  context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        layoutResource = resource;

    }



    private  static  class ViewHolder{
        TextView comment, username, timestamp,reply,likes;
        ShapeableImageView profileImage;
        ImageView like , playVoice;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final  ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();
            holder.comment = (TextView) convertView.findViewById(R.id.comment_show);
            holder.username = (TextView) convertView.findViewById(R.id.comment_username);
            holder.timestamp = (TextView) convertView.findViewById(R.id.comment_time);
            holder.reply = (TextView) convertView.findViewById(R.id.commet_reply);
            holder.like = (ImageView) convertView.findViewById(R.id.comment_like);
            holder.likes = (TextView) convertView.findViewById(R.id.comment_likes);
            holder.profileImage = (ShapeableImageView) convertView.findViewById(R.id.comment_profile_image);
            holder.playVoice = (ImageView) convertView.findViewById(R.id.playVoiceA);




            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        //set the comment
        holder.comment.setText(getItem(position).getComment());

// set the comment or voice
        String voiceUrl = getItem(position).getVoice_url();
        String commentText = getItem(position).getComment();

        if (voiceUrl != null && !voiceUrl.isEmpty()) {
            holder.playVoice.setVisibility(View.VISIBLE);
            holder.comment.setVisibility(View.GONE); // Hide text if it's only audio
            holder.playVoice.setImageResource(R.drawable.ic_stop_comment);

            holder.playVoice.setImageResource(R.drawable.ic_play_comment); // default state
            holder.playVoice.setOnClickListener(v -> {
                Log.d("VOICE_BUTTON", "Clicked!");

                MediaPlayer player = new MediaPlayer();
                try {
                    holder.playVoice.setImageResource(R.drawable.ic_paluse_comment);

                    player.setDataSource(voiceUrl);

                    AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setMode(AudioManager.MODE_NORMAL);
                    audioManager.setSpeakerphoneOn(true);

                    player.setOnPreparedListener(mp -> {
                        Log.d("VOICE_BUTTON", "Audio prepared, starting...");
                        mp.start();
                    });

                    player.setOnCompletionListener(mp -> {
                        holder.playVoice.setImageResource(R.drawable.ic_play_comment);
                        mp.release();
                    });

                    player.prepareAsync(); // async loading
                } catch (Exception e) {
                    Log.e("VOICE_BUTTON", "Playback failed: " + e.getMessage());
                }
            });



        } else {
            holder.playVoice.setVisibility(View.GONE);
            holder.comment.setVisibility(View.VISIBLE);
            holder.comment.setText(commentText); // show comment if no voice
        }



        //set the timestamp difference
        String timestampDifference = getTimestampDifference(getItem(position));
        if (!timestampDifference.equals("Just now")) {
            holder.timestamp.setText(timestampDifference);
        } else {
            holder.timestamp.setText("today");
        }
        //set the username and profile image
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.dbname_user_account_settings))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(getItem(position).getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    holder.username.setText(singleSnapshot.getValue(UserAccountSettings.class).getUsername());
                    ImageLoader.loadImage(singleSnapshot.getValue(UserAccountSettings.class).getProfile_photo(), holder.profileImage, null);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
        try{
            if (position == 0){
                holder.like.setVisibility(View.GONE);
                holder.reply.setVisibility(View.GONE);
                holder.likes.setVisibility(View.GONE);
            }


        }catch (NullPointerException e){
            Log.e(TAG, "getView: NullPointerException: " + e.getMessage());

        }


        return convertView;
    }


    /**
     * Returns a string representing the number of days ago the post was made
     *
     * @return
     */

    private String getTimestampDifference(Comment comment) {
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String result = "Just now"; // default
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();

        final String photoTimestamp = comment.getDate_created();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        try {
            Date photoTime = sdf.parse(photoTimestamp);
            if (photoTime != null) {
                long diffMillis = now.getTime() - photoTime.getTime();

                long diffSeconds = diffMillis / 1000;
                long diffMinutes = diffSeconds / 60;
                long diffHours = diffMinutes / 60;
                long diffDays = diffHours / 24;

                if (diffSeconds < 60) {
                    result = "Just now";
                } else if (diffMinutes < 60) {
                    result = diffMinutes + " minutes ago";
                } else if (diffHours < 24) {
                    result = diffHours + " hours ago";
                } else {
                    result = diffDays + " days ago";
                }
            }
        } catch (ParseException e) {
            Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage());
        }

        return result;
    }

}
