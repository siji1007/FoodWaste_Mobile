plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.foodwaste"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.foodwaste"
        minSdk = 27
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

    buildFeatures{
        viewBinding = true
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
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    //implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.61")
    implementation("androidx.cardview:cardview:1.0.0")
    val lottieVersion = "6.4.0" // Corrected variable name and added a proper version
    implementation("com.airbnb.android:lottie:$lottieVersion")

    implementation("com.google.ai.client.generativeai:generativeai:0.2.0")

    // Required for one-shot operations (to use `ListenableFuture` from Reactive Streams)
    implementation("com.google.guava:guava:31.0.1-android")

    // Required for streaming operations (to use `Publisher` from Guava Android)
    implementation("org.reactivestreams:reactive-streams:1.0.4")

    implementation("com.squareup.picasso:picasso:2.8")

    implementation ("org.osmdroid:osmdroid-android:6.1.6")
    implementation ("org.osmdroid:osmdroid-wms:6.1.6")
    implementation ("org.osmdroid:osmdroid-mapsforge:6.1.6")
    implementation ("org.osmdroid:osmdroid-geopackage:6.1.6")
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("com.google.code.gson:gson:2.8.6")


}