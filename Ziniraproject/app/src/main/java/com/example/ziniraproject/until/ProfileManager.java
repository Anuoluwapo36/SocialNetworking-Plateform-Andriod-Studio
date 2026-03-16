package com.example.ziniraproject.until;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileManager {
    private static final String TAG = "ProfileManager";
    private final FirebaseFirestore mFirestore;

    public ProfileManager() {
        this.mFirestore = FirebaseFirestore.getInstance();
    }

    /**
     * Save personal details (date of birth and gender)
     */
    public void savePersonalDetails(String userId, String dateOfBirth, String gender,
                                    PersonalDetailsCallback callback) {
        Map<String, Object> personalData = new HashMap<>();
        personalData.put("dateOfBirth", dateOfBirth);
        personalData.put("gender", gender);
        personalData.put("updatedAt", FieldValue.serverTimestamp());

        mFirestore.collection("users").document(userId)
                .update(personalData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Personal details saved successfully");
                            callback.onSuccess();
                        } else {
                            Log.w(TAG, "Error saving personal details", task.getException());
                            callback.onFailure(Objects.requireNonNull(task.getException()).getMessage());
                        }
                    }
                });
    }

    /**
     * Callback interface for personal details operations
     */
    public interface PersonalDetailsCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    /**
     * Later you can add more methods like:
     * - updateBio()
     * - updateProfilePicture()
     * - getPersonalDetails()
     */
}