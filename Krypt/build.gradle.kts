// Top-level build file where you can add configuration options common to all sub-projects/modules.
//val classpath = "com.google.gms:google-services:4.3.15"  // Add this line in the dependencies block

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}