import org.gradle.kotlin.dsl.implementation

plugins {
//    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.android.application")
    id("com.google.gms.google-services")}

android {
    namespace = "com.example.ziniraproject"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.ziniraproject"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"



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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

}


dependencies {
    implementation(platform("com.google.firebase:firebase-bom:34.10.0"))
    implementation ("com.github.bumptech.glide:glide:5.0.5")
    implementation(libs.google.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.database)
    implementation(libs.volley)

    implementation("com.google.firebase:firebase-analytics")


    implementation("com.google.android.gms:play-services-ads:25.0.0")
    implementation(libs.firebase.storage)
    implementation(libs.androidx.appcompat)
//
//    implementation("com.google.android.gms:play-services-auth:21.2.0")

//      implementation ("com.google.android.gms:play-services-ads:24.5.0")

//
//
//    implementation("com.google.firebase:firebase-auth:22.3.0")
//    implementation("com.google.firebase:firebase-database:20.3.0")
//    implementation("com.google.firebase:firebase-firestore:24.9.0")
////    implementation("com.google.firebase:firebase-analytics")



//    implementation(libs.firebase.auth.common)


    annotationProcessor ("com.github.bumptech.glide:compiler:5.0.5")
    implementation("com.google.android.material:material:1.13.0")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.3.0")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.core:core-ktx:1.18.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.navigation:navigation-fragment:2.9.7")
    implementation("androidx.navigation:navigation-ui:2.9.7")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    implementation("com.cloudinary:cloudinary-android:3.1.2")
    implementation("com.squareup.okhttp3:okhttp:5.3.2")











// Google Mobile Ads (AdMob)
//    implementation ("com.google.android.gms:play-services-ads:24.2.0")

















}


apply(plugin = "com.google.gms.google-services")








