plugins {
    id("com.android.application") version "8.4.1" apply false
    kotlin("android") version "2.0.0" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
