# Zinira — Android Social Networking Platform

The Android Studio module for **Zinira**, a social networking app built with Java and Firebase. Users register with email verification, post photos, browse a global feed, like and comment (with voice notes), follow other users, search for people, and manage their profile.

> This README documents the `Ziniraproject` module — the Gradle root and Android app. For repo-level notes, see the [parent README](../README.md).

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Android](https://img.shields.io/badge/Android-24%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![AGP](https://img.shields.io/badge/AGP-8.11-02303A?style=for-the-badge&logo=androidstudio)

---

## Table of Contents

- [At a Glance](#at-a-glance)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Module Structure](#module-structure)
- [Architecture](#architecture)
- [Setup](#setup)
- [Configuration](#configuration)
- [Building & Running](#building--running)
- [Data Model](#data-model)
- [Permissions](#permissions)
- [Resources](#resources)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)
- [License](#license)

---

## At a Glance

| | |
|---|---|
| **App name** | Zinira |
| **Package** | `com.example.ziniraproject` |
| **Language** | Java 17 (bytecode target Java 11) |
| **Min / Target SDK** | 24 (Android 7.0) / 36 (Android 16) |
| **Compile SDK** | 36 |
| **Build system** | Gradle Kotlin DSL + version catalog |
| **AGP / Kotlin** | 8.11.0-alpha07 / 2.1.0 |
| **Backend** | Firebase (Auth, Realtime DB, Firestore, Storage, Analytics) |
| **Media hosting** | Cloudinary (unsigned upload preset) via OkHttp |
| **Source size** | 49 Java files, ~9,844 lines, 61 layouts, 56 drawables |

---

## Features

### Authentication
- Email + password sign-up across a **3-step flow** — credentials → username → full details
- **Email verification** required before a user can sign in (`sendVerificationEmail()` on register, `isEmailVerified()` gate on login)
- Auto-redirect to home when a session is already active
- Password change and sign-out in Account Settings

### Content
- **Create a post** — pick from the gallery or capture with the camera, add a caption and tags, upload via Cloudinary
- **Global feed** — infinite scroll with incremental pagination as the user reaches the end
- **Post detail** — full post view with like toggle and an animated heart

### Social graph
- **Follow / unfollow** with live follower and following counters
- **Like** posts, with a "Liked by X, Y and N others" summary
- **Comment** in text or as a **voice note** — recorded on-device with `MediaRecorder`, uploaded as audio to Cloudinary
- **Notifications** generated for likes, comments, and follows

### Profile
- Own profile with a grid of posts and follower stats
- Other-user profile with follow button and post list
- Editable display name, username, bio, phone number, profile photo, and cover photo

### Discovery
- **Live user search** by username, querying the `users` node as you type
- Bottom navigation across six destinations: Home, Message, Create, Search, Notification, Profile

---

## Tech Stack

### Core
| Library | Version |
|---|---|
| Java | 17 source, 11 bytecode target |
| Android Gradle Plugin | 8.11.0-alpha07 |
| Kotlin | 2.1.0 (Compose compiler plugin only) |
| Gradle | 8.x (Kotlin DSL) |
| Android SDK | compile/target 36, min 24 |

### Firebase
| Service | Version | Role |
|---|---|---|
| Firebase Auth | 23.1.0 | Accounts, email verification |
| Realtime Database | 22.0.1 | Posts, follows, followers, notifications |
| Cloud Firestore | 26.1.1 | User records with server timestamps |
| Firebase Storage | 22.0.1 | Reserved for media |
| Firebase Analytics | via BOM 34.10.0 | Usage tracking |
| google-services plugin | 4.4.2 | Config injection |

### Media & networking
| Library | Version | Role |
|---|---|---|
| Cloudinary Android | 3.1.2 | Image + audio upload |
| OkHttp | 5.3.2 | Multipart HTTP requests |
| Glide | 5.0.5 | Image loading |
| Picasso | 2.71828 | Secondary image loading |
| UniversalImageLoader | — | Legacy loader (one helper still uses it) |
| Volley | 1.2.1 | HTTP requests |

### AndroidX
| Library | Version |
|---|---|
| AppCompat | 1.7.1 |
| Material Components | 1.13.0 |
| Core KTX | 1.18.0 |
| ConstraintLayout | 2.2.1 |
| CoordinatorLayout | 1.3.0 |
| Navigation (fragment + UI) | 2.9.7 |
| ViewPager2 | 1.1.0 |
| Lifecycle Runtime KTX | 2.10.0 |
| Compose BOM | 2026.03.00 (Material3 — theming scaffold only) |
| Play Services Ads | 25.0.0 |

> The UI is built entirely with **Views and XML layouts**. Compose is enabled in the build and used only for the `ui/theme` scaffold that Android Studio generates.

---

## Module Structure

```text
Ziniraproject/
├── settings.gradle.kts            # Plugin + dependency repositories
├── build.gradle.kts               # Root build script
├── gradle.properties              # JVM args, AndroidX, Kotlin style
├── gradle/
│   └── libs.versions.toml         # Version catalog (single source of truth)
├── gradlew / gradlew.bat
└── app/
    ├── build.gradle.kts           # App config + dependencies
    ├── google-services.json       # Firebase config (gitignored — see Troubleshooting)
    ├── proguard-rules.pro
    └── src/main/
        ├── AndroidManifest.xml    # 15 activities + permissions
        ├── ic_launcher-playstore.png
        ├── z_logo-playstore.png
        ├── z_name-playstore.png
        ├── java/com/example/ziniraproject/
        │   ├── login/             # LoginActivity, RegisterActivity,
        │   │                      # RegisterNextActivity, RegisterDetailsActivity,
        │   │                      # WelcomeLanding
        │   ├── home/              # HomeActivity, HomeFragment
        │   ├── create/            # CreateActivity, GalleryFragment, PhotoFragment
        │   ├── share/             # ShareActivity, ShareFragment, NextActivity
        │   ├── profile/           # ProfileActivity, ProfileFragment,
        │   │                      # EditProfileFragment, SignOutFragment
        │   ├── like/              # LikesActivity, LikesFragment
        │   ├── message/           # MessageActivity, MessageFragment
        │   ├── search/            # SearchActivity
        │   ├── notification/      # NotificationActivity, NotificationFragment
        │   ├── settings/          # AccountSettingsActivity
        │   ├── dialog/            # ConfirmPasswordDialog
        │   ├── database/          # CloudinaryUploader
        │   ├── models/            # User, Photo, Comment, Like, Notification,
        │   │                      # UserSettings, UserAccountSettings
        │   ├── ui/theme/          # Compose Color.kt, Theme.kt, Type.kt
        │   └── until/             # 26 helpers: adapters, Firebase wrappers,
        │                          # fragments, image & string utilities
        └── res/
            ├── layout/            # 61 layouts
            ├── drawable/          # 56 vector/shape drawables
            ├── drawable-hdpi..xxxhdpi/   # 9 raster UI icons
            ├── mipmap-anydpi-v26/ # 6 adaptive icon definitions
            ├── mipmap-hdpi..xxxhdpi/     # 6 launcher icons per density
            ├── values/            # colors, strings, styles, themes, dimens
            ├── font/              # 4 bundled .ttf files
            ├── menu/              # bottom_navigation_menu.xml
            ├── anim/              # fade_in / fade_out
            └── xml/               # backup_rules, data_extraction_rules
```

> **Note on `until/`** — this package name is a typo for `utils/` that runs through the entire codebase. Renaming it is a safe, purely mechanical change (`until` → `utils` across package declarations, imports, and file paths).

---

## Architecture

Classic **Activity + Fragment** architecture with a single shared Firebase façade.

```
┌─────────────────────────────────────────────┐
│  Activities / Fragments (View + XML UI)      │
│  login · home · create · share · profile     │
│  like · message · search · notification      │
└──────────────────┬──────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────┐
│  until/FireBaseMethod.java  (526 lines)     │
│  Central façade for every Firebase write     │
└──────┬────────────┬─────────────┬───────────┘
       │            │             │
       ▼            ▼             ▼
┌────────────┐ ┌───────────┐ ┌────────────────┐
│   Auth     │ │ Realtime  │ │  Firestore     │
│  (accounts,│ │    DB     │ │ (user records) │
│  verify)   │ │ (posts,   │ │                │
│            │ │  social   │ └────────────────┘
│            │ │  graph,   │
│            │ │  notifs)  │
└────────────┘ └───────────┘
                   │
                   ▼
┌─────────────────────────────────────────────┐
│  database/CloudinaryUploader.java            │
│  images → /image/upload · audio → /video/   │
└─────────────────────────────────────────────┘
```

### Key classes

| Class | Lines | Responsibility |
|---|---|---|
| `until/ViewPostFragment.java` | 838 | Post detail — like toggle, comment thread launch, notification dispatch |
| `until/ViewProfileFragment.java` | 728 | Other-user profile — follow/unfollow, follower counts, follow notification |
| `until/MainFeedAdapter.java` | 586 | Feed adapter with load-more pagination |
| `until/ViewCommentFragment.java` | 585 | Comment thread with `MediaRecorder` voice notes |
| `until/FireBaseMethod.java` | 526 | Central Firebase façade |
| `profile/ProfileFragment.java` | 474 | Own profile — post grid, stats, settings entry |
| `profile/EditProfileFragment.java` | 408 | Edit display name, username, bio, phone, profile & cover photo |
| `create/GalleryFragment.java` | 320 | Gallery picker |
| `share/NextActivity.java` | 316 | Caption + tags + upload step |
| `login/RegisterNextActivity.java` | 310 | Registration step 2 |
| `database/CloudinaryUploader.java` | 170 | Multipart image and audio uploads |

### Data flow — posting a photo

```
GalleryFragment / PhotoFragment
        │  selects or captures an image
        ▼
NextActivity  ── caption, tags
        │
        ▼
CloudinaryUploader.uploadImage()   →  secure_url
        │
        ▼
FireBaseMethod.saveToRealtimeDatabase()
        ├──► /user_photos/{uid}/{photoId}   (profile grid)
        └──► /photos/{photoId}              (global feed)
```

---

## Setup

### Prerequisites

| Requirement | Version |
|---|---|
| Android Studio | Ladybug (2024.2) or newer — required for AGP 8.11 alpha |
| JDK | 17 (bundled with Android Studio) |
| Android SDK | Platform 36 + matching Build Tools |
| Firebase project | Auth, Realtime Database, Firestore enabled |
| Cloudinary account | For media uploads |

### 1. Open the project

```bash
# Point Android Studio at this folder, or:
cd Ziniraproject
```

Open in Android Studio → **Open** → select the folder containing `settings.gradle.kts`. Allow Gradle sync to complete.

### 2. Add your Firebase config

Create a Firebase project, enable **Email/Password** sign-in, and register an Android app with the package name **`com.example.ziniraproject`**. Download `google-services.json` and place it at:

```text
app/google-services.json
```

### 3. Set Realtime Database rules

For local development only:

```json
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```

> ⚠️ Open rules are fine for a demo but unsafe in production. Replace with per-user rules before deploying.

### 4. Configure Cloudinary

Create an account, then add an **unsigned** upload preset named `zinira_mobile_upload` allowing `image` and `video` resource types. Update the constants in `database/CloudinaryUploader.java`:

```java
private static final String CLOUD_NAME = "doqbyyieh";
private static final String UPLOAD_PRESET = "zinira_mobile_upload";
```

Images upload to `/image/upload`; voice notes upload to `/video/upload` (Cloudinary treats `.3gpp` as a video resource). Both parse `secure_url` from the JSON response and post the result back to the main thread via a `Handler`.

---

## Configuration

### `app/build.gradle.kts`

```kotlin
android {
    namespace = "com.example.ziniraproject"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.ziniraproject"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
```

### Version catalog (`gradle/libs.versions.toml`)

All dependency versions are centralised here. To bump a library, edit this one file:

```toml
[versions]
agp = "8.11.0-alpha07"
kotlin = "2.1.0"
firebaseAuth = "23.1.0"
firebaseFirestore = "26.1.1"
firebaseDatabase = "22.0.1"
firebaseStorage = "22.0.1"

[libraries]
firebase-auth = { group = "com.google.firebase", name = "firebase-auth", version.ref = "firebaseAuth" }
firebase-firestore = { group = "com.google.firebase", name = "firebase-firestore", version.ref = "firebaseFirestore" }
```

### Database node names

Every database path is defined in `res/values/strings.xml` — no raw strings in the data layer:

| Key | Value |
|---|---|
| `dbname_users` | `users` |
| `dbname_user_account_settings` | `user_account_settings` |
| `dbname_photos` | `photos` |
| `dbname_user_photos` | `user_photos` |
| `dbname_following` | `following` |
| `dbname_followers` | `follower` |
| `dbname_notifications` | `notification` |

---

## Building & Running

### Android Studio

1. Open the `Ziniraproject` folder.
2. Wait for Gradle sync to finish.
3. Confirm `app/google-services.json` is your own config.
4. Press **Run ▶** and select an emulator or connected device.

### Command line

```bash
cd Ziniraproject

# Build debug APK
./gradlew assembleDebug

# Install on a connected device
./gradlew installDebug

# Lint
./gradlew lint

# Unit tests
./gradlew test
```

**Output:** `app/build/outputs/apk/debug/app-debug.apk`

### Release build

```bash
./gradlew assembleRelease
```

Signing is left at the Android Studio default. Add a `signingConfig` block to `app/build.gradle.kts` before shipping.

---

## Data Model

### Realtime Database

```text
/
├── users/{uid}
│   └── user_id, username, email, phone_number, follower_count
├── user_account_settings/{uid}
│   └── display_name, username, about, phone_number,
│       profile_photo, cover_photo, post, followers, following
├── photos/{photoId}
│   ├── caption, image_path, tags, date_created, user_id, photo_id
│   ├── likes/{uid}
│   └── comments/{commentId}
│       └── comment, user_id, date_created, voice_url
├── user_photos/{uid}/{photoId}        # per-user copy for the profile grid
├── following/{currentUserId}/{targetUserId}
├── follower/{targetUserId}/{currentUserId}
└── notification/{userId}/{notificationId}
    └── type, post_id, comment, timestamp
```

### Firestore

```text
users/{uid}
    └── email, username, firstname, lastname, userId, createdAt
```

### Models

| Model | Fields |
|---|---|
| `Photo` | `caption`, `image_path`, `date_created`, `photo_id`, `user_id`, `tags`, `List<Like> likes`, `List<Comment> comments` |
| `Comment` | `comment`, `user_id`, `date_created`, `voice_url`, `List<Like> likes` |
| `Like` | `user_id` |
| `User` | `user_id`, `username`, `email`, `phone_number`, `follower_count` |
| `UserAccountSettings` | `display_name`, `username`, `about`, `phone_number`, `profile_photo`, `cover_photo`, `post`, `followers`, `following`, `user_id` |
| `Notification` | `type`, `post_id`, `comment`, `timestamp` |

### Timestamps

All dates are generated by `FireBaseMethod.getTimeStamp()` using `yyyy-MM-dd'T'HH:mm:ss'Z'` in the `Asia/Shanghai` timezone. Firestore records use `FieldValue.serverTimestamp()` instead.

---

## Permissions

Declared in `AndroidManifest.xml`:

| Permission | Purpose | Runtime request |
|---|---|---|
| `INTERNET` | Firebase and Cloudinary access | No |
| `CAMERA` | Capture a photo to post | Yes |
| `READ_MEDIA_IMAGES` | Pick a photo from the gallery | Yes |
| `READ_EXTERNAL_STORAGE` (maxSdk 32) | Gallery access on Android 12 and below | Yes |
| `RECORD_AUDIO` | Record voice-note comments | Yes |

`CreateActivity` handles runtime permission requests for camera and gallery. `ViewCommentFragment` requests `RECORD_AUDIO` before recording.

---

## Resources

| Folder | Count | Contents |
|---|---|---|
| `layout/` | 61 | Screens, list items, and reusable snippets |
| `drawable/` | 56 | Vector icons, shapes, gradients, selectors |
| `drawable-hdpi…xxxhdpi/` | 9 | Raster UI icons (comment, heart, mic, send, stop) |
| `mipmap-*/` | 6 per density | `ic_launcher`, `z_logo`, `z_name` + round variants |
| `values/` | 8 | colors, strings, styles, themes, dimens |
| `font/` | 4 | `sacrifice_demo`, `ultra_regular`, `yesteryear_regular`, `ysabeau_sc_medium` |
| `anim/` | 2 | `fade_in`, `fade_out` |

The app theme is `Theme.AppCompat.Light.NoActionBar` (applied directly in the manifest). `Theme.ZiniraProject` is defined in `values/themes.xml` as a Material Light alias.

The launcher icon uses an **adaptive icon** (`mipmap-anydpi-v26`) with `z_logo_foreground` over `z_logo_background`, and the manifest points `android:icon` and `android:roundIcon` at `@mipmap/zinira_logo_transparent`.

---

## Testing

There is currently **no test source set** — `src/` contains only `main/`. The `junit` and `espresso` dependencies are declared in `app/build.gradle.kts` but nothing uses them yet.

To add tests:

```text
app/src/test/java/com/example/ziniraproject/    # unit tests
app/src/androidTest/java/com/example/ziniraproject/    # instrumented tests
```

Run with `./gradlew test` or `./gradlew connectedAndroidTest`.

---

## Troubleshooting

### Build fails: `resource drawable/play not found`

`layout/layout_comment.xml:64` references `@drawable/play`, which does not exist in the project. A drawable named `ic_play_comment.png` **is** present in the density folders. Either point the layout at the existing asset:

```xml
android:src="@drawable/ic_play_comment"
```

or add a `res/drawable/play.xml`.

### Build fails: `gradle-wrapper.properties` missing

`gradlew` and `gradlew.bat` are present, but `gradle/wrapper/gradle-wrapper.properties` and `gradle-wrapper.jar` are not. Regenerate the wrapper from Android Studio: **Gradle → Wrapper → Gradle version → OK**, or run:

```bash
gradle wrapper --gradle-version 8.13
```

### Build fails: plugin `com.android.application` not found

`AGP 8.11.0-alpha07` in `gradle/libs.versions.toml` is an alpha build. Switch to a stable release (for example `8.7.3`) in the catalog.

### Build fails: `google-services` plugin applied twice

`app/build.gradle.kts` declares `id("com.google.gms.google-services")` in the `plugins` block **and** calls `apply(plugin = "com.google.gms.google-services")` at the bottom of the file. Remove the trailing `apply` line.

### App installs but the feed is empty

The database has no seed data. Create the first account through the app — it writes the `users` and `user_account_settings` nodes on registration. There is no `data.sql` or seeding script.

### "This app won't install" on a real device

The package name is `com.example.*`. Google Play rejects `com.example` package names. Rename the package before any Play Store submission.

### Cloudinary uploads fail

Check that the upload preset is set to **Unsigned** and allows the `image` and `video` resource types, and that `CLOUD_NAME` matches your Cloudinary cloud. A misconfigured preset returns a 401 and the callback receives `"Upload failed: Unauthorized"`.

### Login succeeds but the app bounces back to login

`LoginActivity` requires `user.isEmailVerified()`. Check the inbox for the verification email; unverified users are signed out immediately.

---

## License

Released under the [MIT License](LICENSE).

Firebase, Cloudinary, Google Mobile Ads, and Font Awesome are trademarks of their respective owners. Images uploaded by users belong to those users.

---

## Author

Built by **Anuoluwapo Majekobaje Emmanuel** — CS undergraduate at Nanchang University.
