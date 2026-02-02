---
sidebar_position: 1
---

# Environment

Install all software needed to run the project.

:::info

- This guide assumes you are using macOS.
- When seeing this character `~` at the start of the line, you should read it as the expected outcome of the command you should run.

:::

## Installing Xcode

Install Xcode from the AppStore or [Apple Developer](https://developer.apple.com/download/release/).

## Installing Android Studio

Install using [brew](https://brew.sh/)

```shell
brew install android-studio
```

or download from [Android Developer](https://developer.android.com/studio/).

## Installing Java

For running Android apps you need Java. We recommend using the Java version that comes with Android Studio.

We will add the path to Java binaries to the `~/.zshrc` file. To do it run:

```shell
echo 'export PATH="/Applications/Android Studio.app/Contents/jbr/Contents/Home/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

Validate that Java is working by running:

```shell
java --version
```

You should see something like this:

```shell
~ openjdk version "17.0.9" 2023-10-17
~ OpenJDK Runtime Environment Homebrew (build 17.0.9+0)
~ OpenJDK 64-Bit Server VM Homebrew (build 17.0.9+0, mixed mode, sharing)
```

Also confirm the path to Java executable is associated with Android Studio

```shell
which java
```

You should see something like this:

```shell
~ /Applications/Android Studio.app/Contents/jbr/Contents/Home/bin/java
```

Now we need to link the place where Xcode tries to find Java to the one we just installed. To do it run:

```shell
sudo ln -sfn /Applications/Android\ Studio.app/Contents/jbr /Library/Java/JavaVirtualMachines/jbr
```

:::warning

Avoid installing Java with brew to prevent issues with different version of Java when working within Android Studio and Xcode

:::

## Install Kotlin Multiplatform Plugin in Android Studio

Open Android Studio and navigate to Settings/Plugins. Search and install the `Kotlin Multiplatform` plugin.
Make sure you do this before ever opening the project to avoid issues with Gradle caches for ObjC bridged frameworks.

### Congratulations, you are ready to start!

:::tip

You can use [Kdoctor](https://github.com/Kotlin/kdoctor) to confirm your Environment is correctly configured.

**Ignore the warning CocoaPods is not installed, it is not needed for MobileStack**

:::

## Setup the repository

### Via Github Template (recommended)

import NewGithubRepoFromTemplateImg from '@site/static/img/new-github-repo-from-template.png';
import UseThisTemplateImg from '@site/static/img/use-this-template-button.png';

Jump to [Manual Setup](#manual-setup) if you want to manually setup the repository.

Go to the [MobileStack GitHub template](https://github.com/brahyam/mobilestack-kmp) and click on **Use this template**.

<img src={UseThisTemplateImg} style={{ width: '50%', height: 'auto' }} />

Add your repository name, make sure your user is the destination and that `Private` visibility is selected. Then click on `Create repository from template`.

<img src={NewGithubRepoFromTemplateImg} style={{ width: '50%', height: 'auto' }} />

Once created, you have to clone the repository to your local machine.

```shell
git clone [YOUR_REPOSITORY_URL]
```

### Via Manual Setup (skip if used Github Template)

Make sure you have an empty repository created for your app in advance.

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

<Tabs>
<TabItem value="https" label="HTTPS" default>

  GitHub requires a [credentials manager](https://docs.github.com/en/get-started/getting-started-with-git/caching-your-github-credentials-in-git) to clone via HTTPS. Make sure you have it configured before running the following commands.

  Replace `[YOUR_APP_FOLDER_NAME]` with your app name in the following commands:

    ```shell
    git clone https://github.com/brahyam/mobilestack-kmp.git [YOUR_APP_FOLDER_NAME]
    ```

    ```shell
    cd [YOUR_APP_FOLDER_NAME]
    ```

    ```shell
    git remote remove origin
    ```

    ```shell
    git remote add origin [YOUR_GIT_REPOSITORY]
    ```
  </TabItem>
  <TabItem value="ssh" label="SSH">
  Replace `[YOUR_APP_FOLDER_NAME]` with your app name in the following commands:

    ```shell
    git clone git@github.com:brahyam/mobilestack-kmp.git [YOUR_APP_FOLDER_NAME]
    ```

    ```shell
    cd [YOUR_APP_FOLDER_NAME]
    ```

    ```shell
    git remote remove origin
    ```

    ```shell
    git remote add origin [YOUR_GIT_REPOSITORY]
    ```
  </TabItem>
</Tabs>

Now commit all the changes and push them to your repository.

```shell
git add .
```

```shell
git commit -m "Initial commit"
```

```shell
git push -u origin main
```

## Setup future updates (optional)

<Tabs>
<TabItem value="https" label="HTTPS" default>
    ```shell
    git remote add upstream https://github.com/brahyam/mobilestack-kmp.git
    ```

    ```shell
    git remote set-url --push upstream no_push
    ```
  </TabItem>
  <TabItem value="ssh" label="SSH">
    ```shell
    git remote add upstream git@github.com:brahyam/mobilestack-kmp.git
    ```

    ```shell
    git remote set-url --push upstream no_push
    ```
  </TabItem>
</Tabs>

Whenever you want to pull the latest MobileStack changes, run:

```shell
git pull upstream main
```

:::danger

Carefully merge the changes if you have made any local changes to MobileStack files.

:::


:::warning

Make sure you installed the `Kotlin Multiplatform` plugin in Android Studio before opening the project. eg. without opening the project go to `File -> Settings -> Plugins -> Marketplace -> Search for Kotlin Multiplatform` and install it.

:::
