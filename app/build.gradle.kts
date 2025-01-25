plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
<<<<<<< HEAD
    namespace = "com.example.studywithswu"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.studywithswu"
=======
    namespace = "com.anroid.real_studyplanner"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.anroid.real_studyplanner"
>>>>>>> 0c973f5 (studyplanner)
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
<<<<<<< HEAD
=======
    buildFeatures {
        viewBinding = true
    }
>>>>>>> 0c973f5 (studyplanner)
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
<<<<<<< HEAD
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
=======
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
>>>>>>> 0c973f5 (studyplanner)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}