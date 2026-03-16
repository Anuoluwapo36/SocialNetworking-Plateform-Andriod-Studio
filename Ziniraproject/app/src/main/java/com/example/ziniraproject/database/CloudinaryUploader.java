package com.example.ziniraproject.database;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;

import okhttp3.*;

public class CloudinaryUploader {
    private static final String CLOUD_NAME = "doqbyyieh";
    private static final String UPLOAD_PRESET = "zinira_mobile_upload";

    private static final String UPLOAD_URL = "https://api.cloudinary.com/v1_1/" + CLOUD_NAME + "/image/upload";

    public interface UploadCallback {
        void onSuccess(String imageUrl);

        void onFailure(String errorMessage);
    }

    public static void uploadImage(String filePath, UploadCallback callback) {
        OkHttpClient client = new OkHttpClient();

        File imageFile = new File(filePath);
        if (!imageFile.exists()) {
            callback.onFailure("File does not exist");
            return;
        }
        // Create a multipart request body.
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", imageFile.getName(), RequestBody.create(MediaType.parse("image/*"), imageFile))
                .addFormDataPart("upload_preset", UPLOAD_PRESET)
                .build();

        // Create a POST request with the upload URL and request body.

        Request request = new Request.Builder()
                .url(UPLOAD_URL)
                .post(requestBody)
                .build();

        // Asynchronously execute the request.

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                    mainHandler.post(() -> callback.onFailure("Upload failed: " + response.message()));
                    return;
                }

                String responseBody = response.body().string();
                String imageUrl = extractImageUrl(responseBody);
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());

                if (imageUrl != null) {
                    mainHandler.post(() -> callback.onSuccess(imageUrl));
                } else {
                    mainHandler.post(() -> callback.onFailure("Failed to parse image URL"));
                }
            }


        });
    }

    private static String extractImageUrl(String json) {
        String urlKey = "\"secure_url\":\"";
        int start = json.indexOf(urlKey);
        if (start == -1) return null;
        start += urlKey.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }

    public static void uploadBitmap(Bitmap bitmap, UploadCallback callback) {
        try {
            // Save bitmap to a temporary file
            File tempFile = File.createTempFile("upload_", ".jpg");
            java.io.FileOutputStream out = new java.io.FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            // Using the existing uploadImage() method
            uploadImage(tempFile.getAbsolutePath(), new UploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    // Delete temp file after success
                    tempFile.delete();
                    callback.onSuccess(imageUrl);
                }

                @Override
                public void onFailure(String errorMessage) {
                    tempFile.delete();
                    callback.onFailure(errorMessage);
                }
            });

        } catch (IOException e) {
            callback.onFailure("Bitmap conversion failed: " + e.getMessage());
        }
    }


    public static void uploadAudio(String audioPath, UploadCallback callback) {
        OkHttpClient client = new OkHttpClient();

        File audioFile = new File(audioPath);
        if (!audioFile.exists()) {
            callback.onFailure("Audio file does not exist");
            return;
        }

        String uploadUrl = "https://api.cloudinary.com/v1_1/" + CLOUD_NAME + "/video/upload";

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", audioFile.getName(),
                        RequestBody.create(MediaType.parse("audio/3gpp"), audioFile))
                .addFormDataPart("upload_preset", UPLOAD_PRESET)
                .build();

        Request request = new Request.Builder()
                .url(uploadUrl)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Upload failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                    mainHandler.post(() -> callback.onFailure("Upload failed: " + response.message()));
                    return;
                }

                String responseBody = response.body().string();
                String audioUrl = extractImageUrl(responseBody);
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());

                if (audioUrl != null) {
                    mainHandler.post(() -> callback.onSuccess(audioUrl));
                } else {
                    mainHandler.post(() -> callback.onFailure("Failed to parse audio URL"));
                }
            }
        });
    }



}
