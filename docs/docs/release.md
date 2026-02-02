---
sidebar_position: 7
---

# Release

Create release builds and upload them to the app stores.

:::warning

You'll need to have completed your store listings, screenshots, and other metadata before you can submit your app for review.

:::

:::tip

You can generate screenshots for your store listing using [AppMockUp Studio](https://studio.app-mockup.com/).

:::

## Prepare your Android Release Build

1. In the file `androidApp/build.gradle.kts`, change the `versionCode` and `versionName` to the new
   version
2. In Android Studio menu, select `Build` -> `Generate Signed Bundle / APK...`
3. Select `Android App Bundle` and follow the instructions. Make sure you select the release
   keystore created during setup and enter the password. (its the same password for storepass and keypass)

Once the build is finished, you will find the .aab file in the `androidApp/build/outputs/bundle/release` directory.

## Upload to Google Play Console

1. Go to [Google Play Console](https://play.google.com/console/u/0)
2. Select your app from the list
3. Click on `Production` under `Release` in the left menu
4. Click on `Create release`
5. Click on `Browse files` and select the .aab file
6. Release name will be filled automatically once build uploads
7. Create your release notes and save
8. Make sure you added the necessary test account and instructions for the reviewers in
   the `App content`->`App access` section
9. Go to `Publishing overview` and click on `Send changes for review`

## Prepare your iOS Release Build

1. In the file `iosApp/iosApp.xcodeproj`, select your target, switch to `General` tab, `Identity` section and change the
   `Version` and `Build` to the new version eg. 1.0.1 and 1.
2. In Xcode, select `Product` -> `Archive`
3. Click on `Validate App`
4. Click on `Distribute App`
5. Select `TestFlight & App Store` and click `Next`
6. Wait for the upload to finish and to be notified that the build is ready via email

## Upload to App Store Connect

1. Go to [App Store Connect Apps](https://appstoreconnect.apple.com/apps)
2. Select your app from the list
3. Click on `App Store` under `iOS App`
4. Click on `+ Version or Platform`
5. Fill in the version number and click `Create`
6. Click on `iOS App` and select the build you uploaded
7. Fill in the release notes and click `Save`
8. Click on `Submit for Review` and follow the instructions

## Troubleshooting

- `Task :shared:linkReleaseFrameworkIosArm64 FAILED java.lang.OutOfMemoryError: Java heap space`: Increase the heap size by updating gradle.properties to eg. 6GB:

  ```properties
  org.gradle.jvmargs=-Xmx6144M -Dfile.encoding=UTF-8 -Dkotlin.daemon.jvm.options\="-Xmx6144M"
  ```
