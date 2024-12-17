plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.krypt"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.krypt"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.play.services.location)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

// Glide Dependencies
    implementation(libs.glide)              // For loading images
    implementation("com.android.volley:volley:1.2.1") // Add Volley dependency
    implementation("com.google.code.gson:gson:2.8.8") // Add Gson dependency for JSON parsing
    implementation ("org.osmdroid:osmdroid-android:6.1.10")
    implementation ("com.google.firebase:firebase-messaging:24.1.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("androidx.recyclerview:recyclerview:1.2.1")

}
