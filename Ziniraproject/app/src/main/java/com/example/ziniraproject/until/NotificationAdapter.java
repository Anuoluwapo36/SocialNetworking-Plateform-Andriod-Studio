package com.example.ziniraproject.until;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ziniraproject.R;
import com.example.ziniraproject.models.Notification;
import com.example.ziniraproject.until.ImageLoader;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//import java.text.DateUtils;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context context;
    private List<Notification> notificationList;

    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notif = notificationList.get(position);

        // Set message
        holder.message.setText(getMessageFromType(notif.getType()));
        holder.time.setText(getTimeAgo(notif.getTimestamp()));

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.dbname_user_account_settings))
                .child(notif.getFrom_user_id());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.child("username").getValue(String.class);
                String profilePhoto = snapshot.child("profile_photo").getValue(String.class);

                if (username != null) {
                    holder.message.setText(username + " " + getMessageFromType(notif.getType()));
                }

                if (profilePhoto != null && !profilePhoto.isEmpty()) {
                    Glide.with(context)
                            .load(profilePhoto)
                            .placeholder(R.drawable.default_user_gray) // your default placeholder
                            .into(holder.profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("NotificationAdapter", "Failed to fetch user: " + error.getMessage());
            }
        });
    }


    private String getMessageFromType(String type) {
        switch (type) {
            case "like": return "liked your post";
            case "comment": return "commented on your post";
            case "follow": return "started following you";
            default: return "did something";
        }
    }

    private String getTimeAgo(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        long minutes = diff / (60 * 1000);
        if (minutes < 60) return minutes + "m ago";
        long hours = minutes / 60;
        if (hours < 24) return hours + "h ago";
        long days = hours / 24;
        return days + "d ago";
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message, time;
        ImageView profileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.messageTextView);
            time = itemView.findViewById(R.id.timeTextView);
            profileImage = itemView.findViewById(R.id.profileImageView);
        }
    }
}
