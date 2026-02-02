---
sidebar_position: 3
---

# Billing

Configure your in-app products and subscriptions.

:::info

We recommend completing this setup once you have finished adding your features and testing. Or if you don't want to use in-app purchases or subscriptions you can skip this section completely.

:::

## Setup Play Console

1. Go to [Play Store](https://play.google.com/apps/publish/signup/) and create a developer account.
2. Go to the [Google Play Console](https://play.google.com/console/u/0) and create a new app.
3. Release the app to the internal test track. (Follow the [release](/docs/release.md) steps but select the internal test track instead of production)

## Connect RevenueCat with Play Console

1. Go to the [RevenueCat Dashboard](https://app.revenuecat.com/) and create a new project.
2. Add a new Android app to the project
3. Follow [the steps](https://www.revenuecat.com/docs/service-credentials/creating-play-service-credentials) to connect RevenueCat with the Play Console.
4. Follow [RevenueCat steps](https://www.revenuecat.com/docs/getting-started/entitlements/android-products) to create your in-app products and subscriptions.
5. Copy the Android's `Public API Key` from RevenueCat.
6. Go to the [Firebase Console](https://console.firebase.google.com/) / Build / Remote Config and add the following keys:
   - `REVCAT_API_KEY_ANDROID` with the value of the Android `Public API Key` from the previous step.

## Map your RevenueCat products in the app

1. Open the `shared/src/commonMain/kotlin/com/zenithapps/mobilestack/model/Product.kt` file.
2. Rename `STARTER` and `ALL_IN` to your products including their `packageId` (from RevenueCat/Offerings/Packages/Identifier) and `entitlement` (from RevenueCat/Entitlements/Identifier).

   ```kotlin
   enum class Product(val packageId: String, val entitlement: String) {
       YOUR_PRODUCT_NAME("YOUR_PRODUCT_PACKAGE_ID", "YOUR_PRODUCT_ENTITLEMENT"),
       ...
       OTHER("other", "other")
   }
   ```

3. Create a paywall in Revenue Cat with the same entitlement as the product.

## Check products show in Android

1. Run the Android app in a physical device or emulator.
2. Go to the `Purchase` screen.
3. Check that your products are shown.

## Test Android in-app purchases

1. Go to the [Play Store Console](https://play.google.com/console/u/0) select your app and create a test user.
2. Add the test user to the internal test track.
3. Purchase a product with the test user.

---

## Setup App Store Connect

1. Go to the [Apple Developer](https://developer.apple.com/) website and create an account.
2. Go to the [Identifiers](https://developer.apple.com/account/resources/certificates/list) section and create a new App ID.
3. Go to the [Certificates](https://developer.apple.com/account/resources/certificates/list) section and create a new iOS App Development Certificate.
4. Go to the [Certificates](https://developer.apple.com/account/resources/certificates/list) section and create a new iOS App Distribution Certificate.
5. Go to the [Profiles](https://developer.apple.com/account/resources/profiles/list) section and create a new Provisioning Profile for Development.
6. If you will test in a physical device you will need to add the device to the [Devices](https://developer.apple.com/account/resources/devices/list) section.
7. Go to the [App Store Connect](https://appstoreconnect.apple.com/) and create a new app.

## Connect RevenueCat with App Store Connect

1. Go to the [RevenueCat Dashboard](https://app.revenuecat.com/) and add a new iOS app to the project you created.
2. Follow [the steps](https://www.revenuecat.com/docs/service-credentials/itunesconnect-app-specific-shared-secret) to connect RevenueCat with App Store Connect.
3. Follow [RevenueCat steps](https://www.revenuecat.com/docs/getting-started/entitlements/ios-products) to create your in-app products and subscriptions.
4. Copy the iOS's `Public API Key` from RevenueCat.
5. Go to the [Firebase Console](https://console.firebase.google.com/) / Build / Remote Config and add the following key:
   - `REVCAT_API_KEY_IOS` with the value of the iOS `Public API Key`.

## Enable RevenueCat in iOS

Open the `iosApp/iosApp/AppDelegate.swift` file and replace:

   ```swift
      billingProvider = MockBillingProvider()
   ```

   with:

   ```swift
      billingProvider = RevenueCatBillingProvider()
   ```

## Check products show on iOS

1. Run the iOS app in a physical device or simulator.
2. Go to the `Purchase` screen.
3. Check that your products are shown.

## Test iOS in-app purchases

1. Go to the [App Store Connect](https://appstoreconnect.apple.com/) and create a test user.
2. Add the test user to the internal test track.
3. Purchase a product with the test user.
