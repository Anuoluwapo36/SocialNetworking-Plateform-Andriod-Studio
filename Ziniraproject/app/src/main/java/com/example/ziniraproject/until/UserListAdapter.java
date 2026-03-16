package com.example.ziniraproject.until;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ziniraproject.R;
import com.example.ziniraproject.models.User;
import com.example.ziniraproject.models.UserAccountSettings;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;

import java.util.List;

public class UserListAdapter extends ArrayAdapter<User> {
    private  static  final  String TAG = "UserListAdapter";


    private LayoutInflater mInflater;
    private  List<User> mUsers = null;
    private  int layoutResource;
    private  Context mContext;


    public UserListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.mUsers = objects;
    }


    private static  class ViewHolder{
        TextView username, email, fullName;
        ShapeableImageView profileImage;


    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final  ViewHolder holder;
        if (convertView == null){
            convertView = mInflater.inflate(layoutResource, parent,false);
            holder = new ViewHolder();

            holder.username = convertView.findViewById(R.id.username);
//            holder.email = convertView.findViewById(R.id.email);
            holder.fullName = convertView.findViewById(R.id.fullName);
            holder.profileImage = convertView.findViewById(R.id.profileImageId);


            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.username.setText("@"+getItem(position).getUsername());
//        holder.email.setText(getItem(position).getEmail());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query =  reference.child(mContext.getString(R.string.dbname_user_account_settings))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(getItem(position).getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot ) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user " + singleSnapshot.getValue(User.class).toString());
                    ImageLoader.loadImage(singleSnapshot.getValue(UserAccountSettings.class).getProfile_photo(), holder.profileImage, null);
                    holder.fullName.setText(singleSnapshot.getValue(UserAccountSettings.class).getDisplay_name());



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return convertView;
    }
}
