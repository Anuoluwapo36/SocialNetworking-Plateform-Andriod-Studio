package com.example.ziniraproject.until;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.ziniraproject.R;
import com.example.ziniraproject.database.CloudinaryUploader;
import com.example.ziniraproject.home.HomeActivity;
import com.example.ziniraproject.models.Comment;
import com.example.ziniraproject.models.Like;
import com.example.ziniraproject.models.Photo;
import com.example.ziniraproject.models.User;
import com.example.ziniraproject.models.UserAccountSettings;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ViewCommentFragment extends Fragment {

    private static final String TAG = "ViewCommentFragment";

    public ViewCommentFragment() {
        super();
        Log.d(TAG, "ViewCommentFragment: started");
        setArguments(new Bundle());
    }

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private MediaRecorder recorder;
    private String audioFilePath;
    private boolean isRecording = false;
    private boolean isVoiceCommentRecorded = false;



    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;

    //vars
    private Photo mphoto;
    private ArrayList<Comment> mComments;
    private Context mContext;



    //widgets
    private TextView mBackLabel, mCaption, mUsername, mTimestamp, mLikes, usernameUnderPost;
    private ImageView mBackArrow, mPost, mVoiceComment;
    private EditText mCommentInput;
    private ListView mListView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_comment, container, false);
        mBackArrow = view.findViewById(R.id.ic_backArrow);
        mPost = view.findViewById(R.id.postComment);
        mVoiceComment = view.findViewById(R.id.voiceComment);
        mCommentInput = view.findViewById(R.id.commentInput);
        mListView = view.findViewById(R.id.listView);
        mComments = new ArrayList<>();
        mContext = getActivity();



        try {
            mphoto = getPhotoFromBundle();


        } catch (NullPointerException e) {
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage());
        }
        setupFirebaseAuth();

        return view;
    }


    private void setupWidget() {


        CommentListAdapter adapter = new CommentListAdapter(mContext, R.layout.layout_comment, mComments);
        mListView.setAdapter(adapter);

        mPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCommentInput.getText().toString().equals("")) {
                    // Text comment
                    addNewComment(mCommentInput.getText().toString());
                    mCommentInput.setText("");
                    closeKeyboard();
                } else if (isVoiceCommentRecorded && audioFilePath != null) {
                    // Voice comment
                    File audioFile = new File(audioFilePath);
                    int MIN_FILE_SIZE_BYTES = 0;
                    if (!audioFile.exists() || audioFile.length() < MIN_FILE_SIZE_BYTES) {
                        Log.e(TAG, "Invalid audio file: " + audioFilePath + ", size: " + audioFile.length());
                        isVoiceCommentRecorded = false;
                        audioFilePath = null;
                        Toast.makeText(mContext, "Invalid audio recording", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    CloudinaryUploader.uploadAudio(audioFilePath, new CloudinaryUploader.UploadCallback() {
                        @Override
                        public void onSuccess(String audioUrl) {
                            addVoiceComment(audioUrl);
                            isVoiceCommentRecorded = false;
                            audioFilePath = null;
                            Toast.makeText(mContext, "Voice comment posted", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Log.e(TAG, "Audio upload failed: " + errorMessage);
                            Toast.makeText(mContext, "Upload failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.d(TAG, "onClick: comment input field is empty");
                    mCommentInput.setError("Comment or voice message is required");
                    mCommentInput.requestFocus();
                }
            }
        });



        mVoiceComment.setOnClickListener(v -> {
            if (!isRecording) {
                startRecording();
            } else {
                stopRecording();
            }
        });

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: navigating back");

                if (getCallingActivityFromBundle().equals(getString(R.string.home_activity))){
                    getActivity().getSupportFragmentManager().popBackStack();
                    ((HomeActivity) getActivity()).showLayout();
                }else {
                    getActivity().getSupportFragmentManager().popBackStack();
                }


            }
        });


    }

    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void addNewComment(String newComment) {
        Log.d(TAG, "addNewComment: adding a new comment");
        String commentID = mRef.push().getKey();
        Comment comment = new Comment();
        comment.setComment(newComment);
        comment.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        comment.setDate_created(getTimeStamp());

        //insert into photo node
        mRef.child(getString(R.string.dbname_photos))
                .child(mphoto.getPhoto_id())
                .child(getString(R.string.field_comments))
                .child(commentID)
                .setValue(comment);

        //insert into user_ photo node
        mRef.child(getString(R.string.dbname_user_photos))
                .child(mphoto.getUser_id())
                .child(mphoto.getPhoto_id())
                .child(getString(R.string.field_comments))
                .child(commentID)
                .setValue(comment);


    }


    private void addVoiceComment(String voiceUrl) {
        String commentID = mRef.push().getKey();
        Comment comment = new Comment();
        comment.setVoice_url(voiceUrl);
        comment.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        comment.setDate_created(getTimeStamp());
        comment.setVoice_url(voiceUrl);

        mRef.child(getString(R.string.dbname_photos))
                .child(mphoto.getPhoto_id())
                .child(getString(R.string.field_comments))
                .child(commentID)
                .setValue(comment);

        mRef.child(getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mphoto.getPhoto_id())
                .child(getString(R.string.field_comments))
                .child(commentID)
                .setValue(comment);
    }


    private String getTimeStamp() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        String dateCreated = sdf.format(new Date());

        return dateCreated;
    }


    /**
     * Returns a string representing the number of days ago the post was made
     *
     * @return
     */

    private String getTimestampDifference() {
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String result = "Just now"; // default
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();

        final String photoTimestamp = mphoto.getDate_created();

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

    /**
     * retrieve the photo from the incoming bundle from profileActivity interface
     *
     * @return
     */

    private Photo getPhotoFromBundle() {
        Log.d(TAG, "getPhotoFromBundle: arguments: " + getArguments());
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getParcelable(getString(R.string.photo));

        } else {
            return null;
        }

    }

    /**
     * retrieve the photo from the incoming bundle from profileActivity interface
     *
     * @return
     */

    private String getCallingActivityFromBundle() {
        Log.d(TAG, "getPhotoFromBundle: arguments: " + getArguments());
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getString(getString(R.string.home_activity));

        } else {
            return null;
        }

    }





    /*
     * --------------------------------------firebase------------------------------------------------------------
     *
     *
     */


    /**
     * responsible for setting up firebase auth
     */
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: started firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged: user signed in" + user.getUid());

                } else {
                    Log.d(TAG, "onAuthStateChanged: user signed out");

                }
            }
        };

        if (mphoto.getComments().size() == 0){

            mComments.clear();
            Comment firstComment = new Comment();
            firstComment.setComment(mphoto.getCaption());
            firstComment.setUser_id(mphoto.getUser_id());
            firstComment.setDate_created(mphoto.getDate_created());
            mComments.add(firstComment);
            setupWidget();
        }
        mRef.child(mContext.getString(R.string.dbname_photos))
                .child(mphoto.getPhoto_id())
                .child(mContext.getString(R.string.field_comments))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Query query = mRef
                                .child(mContext.getString(R.string.dbname_photos))
                                .orderByChild(mContext.getString(R.string.field_photo_id))
                                .equalTo(mphoto.getPhoto_id());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                                    Photo photo = new Photo();
                                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
                                    photo.setCaption(objectMap.get(mContext.getString(R.string.field_caption)).toString());
                                    photo.setTags(objectMap.get(mContext.getString(R.string.field_tags)).toString());
                                    photo.setPhoto_id(objectMap.get(mContext.getString(R.string.field_photo_id)).toString());
                                    photo.setUser_id(objectMap.get(mContext.getString(R.string.field_user_id)).toString());
                                    photo.setDate_created(objectMap.get(mContext.getString(R.string.field_date_created)).toString());
                                    photo.setImage_path(objectMap.get(mContext.getString(R.string.field_image_path)).toString());


                                    mComments.clear();
                                    Comment firstComment = new Comment();
                                    firstComment.setComment(mphoto.getCaption());
                                    firstComment.setUser_id(mphoto.getUser_id());
                                    firstComment.setDate_created(mphoto.getDate_created());
                                    mComments.add(firstComment);

                                    for (DataSnapshot dSnapshot : singleSnapshot.child(mContext.getString(R.string.field_comments)).getChildren()) {
                                        Comment comment = new Comment();
                                        comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                                        comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                                        comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                                        comment.setVoice_url(dSnapshot.getValue(Comment.class).getVoice_url());
                                        mComments.add(comment);


                                    }

                                    photo.setComments(mComments);
                                    mphoto = photo;
                                    setupWidget();

                                    //user in future to get likes comment

//                    List<Like> likeList = new ArrayList<Like>();
//                    for (DataSnapshot dSnapshot : singleSnapshot.child(getString(R.string.field_likes)).getChildren()) {
//                        Like like = new Like();
//                        like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
//                        likeList.add(like);
//
//
//                    }


                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d(TAG, "onCancelled: query cancel");

                            }
                        });

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    @Override
    public void onStart() {

        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);

        }
    }




    private void startRecording() {
        // Check permission first
        if (getContext() == null || getActivity() == null) return;

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION
            );
            Log.e(TAG, "Permission not granted for RECORD_AUDIO");
            return;
        }

        try {
            audioFilePath = getActivity().getExternalCacheDir().getAbsolutePath() + "/voice_comment.3gp";
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(audioFilePath);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.prepare();
            recorder.start();
            isRecording = true;
            Log.d(TAG, "Recording started");
        } catch (IOException | IllegalStateException e) {
            Log.e(TAG, "startRecording: failed", e);
        }
    }


    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
            isRecording = false;
            isVoiceCommentRecorded = true; // <-- Mark as recorded
            Log.d(TAG, "Recording stopped");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording(); // Permission granted, retry recording
            } else {
                Log.e(TAG, "Microphone permission denied");
            }
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    101);
        }
    }

}
