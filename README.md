* How to add a feature eg. new screen?

** Create a new Model (If fetching data from DB)

- Create a new serializable data class with the fields you'll need to display in the UI
- Create a new serializable data class with the fields as they come from the server (DTO). This is
  useful to avoid coupling the UI with the server response
- Create a new extension mapping function to convert the DTO to the Model

** Create a new Component

- Start with the Interface, add a model containing the data that will be dynamic eg. fetched from
  the server / updating the UI
- Add functions for every action users can perform eg. back button tap.
- Add a sealed class with the Outputs of the screen eg. navigation: go back
- Create a Default implementation of your interface that receives a context, analytics, onOutput
  function and the repository/provider you'll use to fetch data
- Implement all functions of the interface

** Create a Repository or Provider (Used to fetch data from your backend or another provider)

- Create a new interface with the functions you'll need to fetch data eg. getItems()

** Create a new Screen

* How to release the apps to the stores?

** Android
*** Prepare Release Build

1. In the file `androidApp/build.gradle.kts`, change the `versionCode` and `versionName` to the new
   version
2. In Android Studio menu, select `Build` -> `Generate Signed Bundle / APK...`
3. Select `Android App Bundle` and click `Next`
4. Select the module and click `Next`
5. Create a new keystore or use an existing one
6. Select `release` variant and click `Create`
7. The APK will be generated in the `release` folder

*** Upload to Google Play Console

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

** iOS
*** Prepare Release Build

1. In the file `iosApp/iosApp.xcodeproj`, select your target, switch to `info` tab and change the
   `Bundle version string` and `Bundle version` to the new version
2. In Xcode, select `Product` -> `Archive`
3. Click on `Validate App`
4. Click on `Distribute App`
5. Select `TestFlight & App Store` and click `Next`
6. Wait for the upload to finish and to be notified that the build is ready via email

*** Upload to App Store Connect

1. Go to [App Store Connect Apps](https://appstoreconnect.apple.com/apps)
2. Select your app from the list
3. Click on `App Store` under `iOS App`
4. Click on `+ Version or Platform`
5. Fill in the version number and click `Create`
6. Click on `iOS App` and select the build you uploaded
7. Fill in the release notes and click `Save`
8. Click on `Submit for Review` and follow the instructions

* Troubleshooting
  ** iOS
  *** App build/start/archive stuck loading
* Restart Xcode
* Restart your computer
* Clean the build folder by selecting `Product` -> `Clean Build Folder`
* Delete the `DerivedData` folder by running `rm -rf ~/Library/Developer/Xcode/DerivedData` in the
  terminal