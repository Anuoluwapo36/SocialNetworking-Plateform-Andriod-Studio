package com.example.ziniraproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class UserAccountSettings implements Parcelable {
    private  String about;
    private String display_name;
    private long post;
    private long followers;
    private long following;

    private String profile_photo;
    private String cover_photo;


    private String username;
    private String user_id;


    public UserAccountSettings(String about, String display_name, long post, long followers, long following, String profile_photo, String cover_photo, String username, String user_id) {
        this.about = about;
        this.display_name = display_name;
        this.post = post;
        this.followers = followers;
        this.following = following;
        this.profile_photo = profile_photo;
        this.cover_photo = cover_photo;
        this.username = username;
        this.user_id = user_id;
    }

    protected UserAccountSettings(Parcel in) {
        about = in.readString();
        display_name = in.readString();
        post = in.readLong();
        followers = in.readLong();
        following = in.readLong();
        profile_photo = in.readString();
        cover_photo = in.readString();
        username = in.readString();
        user_id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(about);
        dest.writeString(display_name);
        dest.writeLong(post);
        dest.writeLong(followers);
        dest.writeLong(following);
        dest.writeString(profile_photo);
        dest.writeString(cover_photo);
        dest.writeString(username);
        dest.writeString(user_id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserAccountSettings> CREATOR = new Creator<UserAccountSettings>() {
        @Override
        public UserAccountSettings createFromParcel(Parcel in) {
            return new UserAccountSettings(in);
        }

        @Override
        public UserAccountSettings[] newArray(int size) {
            return new UserAccountSettings[size];
        }
    };

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public UserAccountSettings() {

    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public long getPost() {
        return post;
    }

    public void setPost(long post) {
        this.post = post;
    }

    public long getFollowers() {
        return followers;
    }

    public void setFollowers(long followers) {
        this.followers = followers;
    }

    public long getFollowing() {
        return following;
    }

    public void setFollowing(long following) {
        this.following = following;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getCover_photo() {
        return cover_photo;
    }

    public void setCover_photo(String cover_photo) {
        this.cover_photo = cover_photo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    @NonNull
    @Override
    public String toString() {
        return "UserAccountSettings{" +
                "about'" + about + '\'' +
                ", display_name='" + display_name + '\'' +
                ", post=" + post +
                ", followers=" + followers +
                ", following=" + following +
                ", profile_photo='" + profile_photo + '\'' +
                ", cover_photo='" + cover_photo + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
