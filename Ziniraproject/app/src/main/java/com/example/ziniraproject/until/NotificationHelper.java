//package com.example.ziniraproject.until;
//
//
//
//import android.content.Context;
//
//import androidx.annotation.Nullable;
//
//import com.example.ziniraproject.R;
//import com.example.ziniraproject.models.Notification;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.FirebaseDatabase;
//
//public class NotificationHelper {
//    private static Context mContext;
//
//    public static void sendNotification(String toUserId, String type, @Nullable String postId, @Nullable String comment) {
//        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        if (toUserId.equals(currentUserId)) return;
//
//
//
//        FirebaseDatabase.getInstance().getReference()
//                .child(mContext.getString(R.string.dbname_notifications))
//                .child(toUserId)
//                .push()
//                .setValue(notif);
//    }
//}
