# Zinira — Social Networking Platform (Android)

A social networking app for Android, built with Java and Firebase. People create an account, share photos, scroll through a feed of what others have posted, like and comment on what they like, follow other users, and manage their own profile.

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Android](https://img.shields.io/badge/Android-24%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Gradle](https://img.shields.io/badge/Gradle-8.x-02303A?style=for-the-badge&logo=gradle)

---

## What It Does

Someone opens the app for the first time and signs up with an email address and password. A verification email goes out, and they confirm their address before they can sign in. Once in, they set a username and fill in the rest of their details.

The main screen is a feed of photos posted by everyone on the app. Scrolling down brings in more posts automatically. Tapping a post opens it full screen, where they can like it or leave a comment.

Comments can be typed or recorded as a voice note — the app records the audio on the device and uploads it, so a comment can be spoken rather than typed.

Following someone adds them to a list, and their posts can be browsed from their profile. The follower and following counts update straight away. The people they already follow can be listed from the profile screen.

As they type in the search bar, matching usernames appear as suggestions and photos from matching accounts show up alongside, so a person can be found quickly.

A notifications screen collects what has happened to them — a post was liked, a comment was left, someone started following them.

Their own profile shows their details, their posts in a grid, and their cover photo, all of which can be edited. Account settings handle changing the password and signing out.

---

## Features

**Accounts**

- Three-step sign-up — credentials, then a username, then the remaining details
- Email verification required before signing in
- Sign in, sign out, and password change

**Sharing**

- Post a photo from the gallery or take a new one with the camera
- Add a caption and tags to a post
- Upload images and voice notes through Cloudinary

**Social**

- Global feed of posts with automatic loading as the user scrolls
- Like a post, with the heart animating and a count of who else liked it
- Comment on a post by typing or recording a voice note
- Follow and unfollow other users
- Live follower and following counts
- Notifications for likes, comments, and new followers

**Finding people**

- Search that suggests usernames as you type
- Photo results shown beside the name suggestions
- Browse your own posts and the posts of people you follow

**Profile**

- Editable display name, username, bio, phone number, profile photo, and cover photo
- Posts displayed as a grid
- Dedicated screen for the posts a user has liked

---

## How It's Built

The app uses Firebase for its backend, split across the services that suit each job. Firebase Authentication handles accounts and email verification. The Realtime Database holds the posts, the follow relationships, and the notifications. Firestore holds the user records. Firebase Analytics tracks usage.

Photos and voice notes are hosted on Cloudinary rather than Firebase Storage. The app uploads them there and stores the returned link in the database, which keeps the media files separate from the app's own data.

All of the Firebase work goes through one shared helper class, so the rest of the app never talks to the services directly. Each screen is an Activity or Fragment, and the navigation between them is handled with a bottom navigation bar and tabbed view pagers.

Images are loaded with Glide, and the screens are built with the standard Android view system using XML layouts.

---

## Tech Stack

| | |
|---|---|
| **Language** | Java 17 |
| **Platform** | Android, min SDK 24, target SDK 36 |
| **Build** | Gradle with a version catalog |
| **Backend** | Firebase Authentication, Realtime Database, Firestore, Analytics |
| **Media hosting** | Cloudinary |
| **Libraries** | OkHttp, Glide, Picasso, Volley, Material Components, Navigation, ViewPager2 |

---

## Running the Project

Open the `Ziniraproject` folder in Android Studio and let the Gradle sync finish, then press Run and pick an emulator or a connected device.

The app needs its own Firebase project and a Cloudinary account for image uploads. Both are set up through their own dashboards, and the values go into the Firebase config file and the upload helper class.

---

## License

Released under the [MIT License](LICENSE).

Firebase, Cloudinary, Google Mobile Ads, and Font Awesome are trademarks of their respective owners. Images uploaded by users belong to those users.

---

## Author

Built by **Anuoluwapo Majekobaje Emmanuel** — CS undergraduate at Nanchang University.
