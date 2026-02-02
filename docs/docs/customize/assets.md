---
sidebar_position: 1
---

# Assets

Setup your app icon, fonts and images.

## Icons

:::tip

You can use the [Icon Kitchen App](https://icon.kitchen/) to generate all the necessary icon sizes for your apps.

:::

1. In Android Studio, you'll need to replace the `mipmap` folder files in `androidApp/src/main/res` with your icons. If you used Icon Kitchen, you can replace all `mipmap` folders with the ones in the generated zip.
2. In Xcode, select the `Assets.xcassets` file and replace each icon with the corresponding one from from your assets or the Icon Kitchen zip file.

## Fonts

:::tip

You can use the [Google Fonts](https://fonts.google.com/) to find and download custom fonts.

:::

1. Add your custom fonts in .ttf format to the `shared/src/commonMain/composeResources/font` directory.
2. Go to `shared/src/commonMain/kotlin/com/zenithapps/mobilestack/ui/style/Typography.kt` and add your font to the `FontFamily` object with the corresponding weight.

```kotlin
@Composable
private fun getFontFamily() = FontFamily(
    Font(resource = Res.font.[YOUR_FONT_NAME], weight = FontWeight.Normal),
    Font(resource = Res.font.[YOUR_FONT_NAME], weight = FontWeight.Bold)
)
```

## Images

1. Add your images to the `shared/src/commonMain/composeResources/drawable` directory.
2. Use the `painterResource` function to load the image in your composable.

```kotlin
Image(
   painter = painterResource(Res.drawable.[YOUR_IMAGE_NAME]),
   contentDescription = "[YOUR_IMAGE_DESCRIPTION]"
)
```

:::tip

The easiest way to import images and svgs is to use the Resource Manager in Android Studio. Make sure you move them to `composeResources/drawable` after you import them.

:::
