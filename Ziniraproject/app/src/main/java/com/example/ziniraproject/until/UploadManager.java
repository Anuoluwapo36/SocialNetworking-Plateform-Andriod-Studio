////package com.example.ziniraproject.until;
////
////import android.util.Log;
////
////import androidx.annotation.NonNull;
////
////import com.google.android.gms.tasks.OnCompleteListener;
////import com.google.android.gms.tasks.Task;
////import com.google.firebase.auth.FirebaseAuth;
////import com.google.firebase.firestore.FieldValue;
////import com.google.firebase.firestore.FirebaseFirestore;
////
////import java.util.HashMap;
////import java.util.Map;
////import java.util.Objects;
////
////public class UploadManager {
////    private static final String TAG = "UploadManager";
////    private final FirebaseFirestore mFirestore;
////
////    public UploadManager() {
////        this.mFirestore = FirebaseFirestore.getInstance();
////    }
////
////    /**
////     * Save image upload details (imageUrl, caption, userId, timestamp)
////     */
////    public void saveUpload(String imageUrl, String caption, UploadCallback callback) {
////        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
////                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
////                : null;
////
////        if (userId == null) {
////            if (callback != null) callback.onFailure("User not logged in");
////            return;
////        }
////
////        Map<String, Object> uploadData = new HashMap<>();
////        uploadData.put("imageUrl", imageUrl);
////        uploadData.put("caption", caption);
////        uploadData.put("userId", userId);
////        uploadData.put("timestamp", FieldValue.serverTimestamp());
////
////        // Auto-generated document ID
////        mFirestore.collection("uploads").document()
////                .set(uploadData)
////                .addOnCompleteListener(new OnCompleteListener<Void>() {
////                    @Override
////                    public void onComplete(@NonNull Task<Void> task) {
////                        if (task.isSuccessful()) {
////                            Log.d(TAG, "Upload saved successfully");
////                            if (callback != null) callback.onSuccess();
////                        } else {
////                            Log.w(TAG, "Error saving upload", task.getException());
////                            if (callback != null)
////                                callback.onFailure(Objects.requireNonNull(task.getException()).getMessage());
////                        }
////                    }
////                });
////    }
////
////    /**
////     * Callback interface for upload save operations
////     */
////    public interface UploadCallback {
////        void onSuccess();
////        void onFailure(String errorMessage);
////    }
////}
//
//import android.widget.Toast;
//
//import com.example.ziniraproject.database.CloudinaryUploader;
//import com.example.ziniraproject.share.NextActivity;if (imgUrl == null || imgUrl.isEmpty()) {
//        Toast.makeText(NextActivity .this, "Image path is empty or invalid",Toast.LENGTH_SHORT).show();
//                    return; // Stop to avoid crash
//                            }
//
//                            CloudinaryUploader.uploadImage(imgUrl, new CloudinaryUploader.UploadCallback() {
//    @Override
//    public void onSuccess(String imageUrl) {
//        saveToRealtimeDatabase(imageUrl, mCaption.getText().toString());
//
//        uploadManager.saveUpload(imageUrl, mCaption.getText().toString(), new UploadManager.UploadCallback() {
//            @Override
//            public void onSuccess() {
//                Toast.makeText(NextActivity.this, "Saved to Firestore", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//                Toast.makeText(NextActivity.this, "Firestore save failed: " + errorMessage, Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//    @Override
//    public void onFailure(String errorMessage) {
//        Toast.makeText(NextActivity.this, "Cloudinary upload failed: " + errorMessage, Toast.LENGTH_LONG).show();
//    }
//});