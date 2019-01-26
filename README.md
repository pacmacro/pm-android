# Pac Macro Mobile Application (Android)

[![License](https://img.shields.io/github/license/mashape/apistatus.svg?maxAge=2592000)](https://github.com/pacmacro/pm-android/blob/master/LICENSE)

Android mobile application for playing the game Pac Macro.  
The official specification for the game can be found [here](https://github.com/pacmacro/pm-specification).

## Setup

Install [Android Studio](https://developer.android.com/studio/) and clone the project to your local machine:
```shell
git clone https://github.com/pacmacro/pm-android
```

### Google Maps API Key for Dev Builds

Generate a [Google Maps SDK API key (step 1)](https://developers.google.com/maps/documentation/android-sdk/signup).

In the file `app/build.gradle`, replace the value of the key `android.buildTypes.debug.manifestPlaceholders` with the API key.

Build and run the Android application on a physical or virtual device.

___

For more information, see the [wiki](https://github.com/pacmacro/pm-android/wiki) for the Android application.

## Credits

This project is brought to you in part by:

* ![Mobify logo](readme-img/mobify-logo.png) [Mobify](https://www.mobify.com/about/), a sponsor of this Pac Macro implementation
