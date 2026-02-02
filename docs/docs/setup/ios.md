---
sidebar_position: 2
---

# iOS App

Configure and run the iOS App for the first time.

:::info

You need to finish [setting up the Android app before starting with the iOS app](/docs/setup/android).

This is because MobileStack uses Kotlin Multiplatform for all business logic, UI and navigation.

:::

## Update project name, app name and bundle identifier

Open the iOs project by opening Xcode and selecting the `iosApp/iosApp.xcodeproj` file.

1. Select the top level `iosApp` project file in the file tree (blue icon).
2. Press `Enter` to rename the project to your app name.
3. Go to the `General tab` / `Identity` section and replace `MobileStack` with your app name. (this is the name that will be displayed in the phone)
4. Go to the `Signing & Capabilities` tab and replace `com.zenithapps.mobilestack.ios` with your bundle identifier.
5. On the left side of the Xcode windows there is a `TARGETS` list. Double click on the current target name `MobileStack` and replace with your app name.
6. On the top bar of the Xcode window there is an icon that says `MobileStack`. Click on it, select `manage schemes`, press on it to highlight it, press enter to rename it and use your app name, click outside the window to save the changes. (might need to restart Xcode)

:::tip

For bundle identifier is better to use a url that you own. It will be your unique identifier in the app storel;l; and cannot be changed once your app is published.

:::

:::warning

If the scheme name does not change, you might have to restart Xcode.

:::

## Configure Firebase services

1. Go to the [Firebase Console](https://console.firebase.google.com/) and select the project you created while configuring the Android app.
2. From your project overview or `project settings / general` tab, click on the `Add app` button and click on the `iOS` icon to add a new iOS app to the project
3. Follow the instructions to download the `GoogleService-Info.plist` file and place it in the `iosApp` directory. (Skip filling the App Store Id field, also adding and initialising the SDKs, everything is included and initialised already in MobileStack).
4. Make sure you finish the setup by clicking Next on all steps and then Add App.

## Running the project

1. Make sure your scheme is selected in the top center bar.
2. Select the device you want to run the app by clicking on Xcode top center bar right after the scheme.
3. Click on the play button to run the app.
4. (After a while) You will see MobileStack running on your device.

:::info

Normally debug builds are slower to initialise than release builds. So you might see a white/black screen for a few seconds before the app starts.

:::

## Troubleshooting

- `No such module 'Shared'` -> Make sure you have opened the `iosApp.xcodeproj` file and not the `iosApp.xcworkspace` file.
- `GoogleService-Info.plist not found` -> Make sure you have placed the file in the `iosApp` directory and that the file name is correct.
- `Multiple commands produce '/Users/.../iosApp/GoogleService-Info.plist'` -> If you see the file in the file tree please delete it and run the app again.
- `Configuration.storekit: No such file or directory` -> If you see the file in the file tree please delete it and run the app again.
- `Unknown reference` -> If you see this error, you should clear all the gradle caches and derived data, install the Kotlin Multiplatform plugin in Android Studio and rebuild the project.
- `Command PhaseScriptExecution failed with a nonzero exit code` -> Use the report navigator to inspect the last build in detail and find the real error message.
- `Java environment not found` -> Make sure linked AndroidStudio's Java to Xcode's Java location as suggested in the [Java setup instructions](/docs/environment#installing-java).
- `No profile for team 'XXX' matching 'match Development [bundleID]' found: Xcode couldn't find any provisioning profiles matching 'XXX/match Development [bundleId]'.` -> Select a simulator as the target device and run the app.
- `The operation couldn’t be completed. Unable to locate Java Runtime` -> Make sure you have selected a simulator as the target device and run the app.
- `/build/ios/SourcePackages/checkouts/firebase-ios-sdk/Crashlytics/run: No such file or directory` -> Add `${SHARED_PRECOMPS_DIR%/Build/*}/SourcePackages/checkouts/firebase-ios-sdk/Crashlytics/run` to the Crashlytics `Run` build phase.
- `Missing package product 'X'` -> Delete derived data by running `rm -rf ~/Library/Developer/Xcode/DerivedData` in the terminal and run the app again.