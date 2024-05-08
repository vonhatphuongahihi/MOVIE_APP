plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.googleGmsGoogleServices) apply false
}

buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath (libs.google.services)
    }
}
