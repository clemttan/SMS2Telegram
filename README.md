# Minimalist Android SMS to Telegram Forwarder

No Ads, No Pro/Premium, No Tracking, No Unnecessary Permissions.

Free & Open Source under MIT license, built for common good.

Support Android 5.0+ with no dependencies (Not even Play Store).

![Screenshots](docs/images/cover.png)

## Motivation

I use old Android phone to receive OTP from multiple numbers.

Existing apps from Play Store are full of ads, paywalls and some require unnecessary permissions.

Given that SMS OTP are sensitive data and leaking it poses critical security risk.

I decide to make an FOSS Telegram to SMS Forwarder, and share it freely for common good.

Everyone are free to review the code, compile it themselves and use it risk-free.

## How to use

In the next few steps, you will create your own Telegram Bot with official chatbot.

Therefore your data will not be shared with any third party servers.

### Step 1: Create Telegram Bot API Key

Start a chat with [@BotFather](https://t.me/botfather)

You:
> /newbot

BotFather:
> Alright, a new bot. How are we going to call it? Please choose a name for your bot.

You:
> SMS2Telegram

BotFather:
> Good. Now let's choose a username for your bot. It must end in `bot`. Like this, for example: TetrisBot or tetris_bot.

You:
> SMS2TelegramDemoBot

BotFather:
> Done! Congratulations on your new bot. You will find it at t.me/SMS2TelegramDemoBot. You can now add a description, about section and profile picture for your bot, see /help for a list of commands. By the way, when you've finished creating your cool bot, ping our Bot Support if you want a better username for it. Just make sure the bot is fully operational before you do this.

> Use this token to access the HTTP API:
> 8284599999:AAGbDp-Lk1IJFaoH0fuO6skp3rc-XXX_XXX
> Keep your token secure and store it safely, it can be used by anyone to control your bot.

> For a description of the Bot API, see this page: https://core.telegram.org/bots/api

Now we got the API Token: *8284599999:AAGbDp-Lk1IJFaoH0fuO6skp3rc-XXX_XXX*

### Step 2: App setup

From the previous step, click on the link of your new bot: *t.me/SMS2TelegramDemoBot*, and click on the big "START" button or send any message to it.

Copy the API Token you obtained from previous step into the "Telegram Bot API Key" field.

Click on the "Find Chat ID" button, and select your Telegram username. The "Telegram Chat ID" field will be automatically filled.

Then click on "Validate and Continue" button to verify and complete the setup.

## Download

Although you are encouraged to build it from source code for maximum security, Play Store and APK are available.

APK Download: [Releases](https://github.com/imTigger/SMS2Telegram/releases/latest)

Play Store: [Play Store](https://play.google.com/store/apps/details?id=com.tigerworkshop.sms2telegram)

## Build from source

Just clone the project and use the latest version of Android Studio to import it.

### Forward only selected senders

Open **Allowed senders** from the three-dot menu (available before completing
Telegram setup), or from the forwarding status screen. Add the exact sender
name or phone number, then enable **Only forward allowed senders**. For example,
add `HelloBike` to allow that sender only. Use **Remove** to delete an entry.
Changes save automatically and survive restarting the app.

- Names match the complete SMS originating address, ignoring case and outer
  whitespace. Message contents are never used to decide the sender.
- Phone numbers must use the exact country code and formatting shown by the SMS.
  No substring, wildcard, contact-name, or country-code guessing is performed.
- An enabled empty list forwards no SMS. Disabling the filter allows all senders
  and requires confirmation. Existing installations keep forwarding all senders
  until filtering is enabled.
- Disallowed incoming SMS are ignored before queuing. Pending messages are
  checked again before each delivery, so messages from removed senders are
  discarded on retry. A send already in progress cannot be recalled.
- Manually initiated test/configuration messages are not SMS and are not filtered.
- Android SMS/OTP access restrictions still apply.

### Build and test

With JDK 17, Android SDK Platform 36 and Gradle 8.13 installed:

```sh
gradle testDebugUnitTest lintDebug assembleDebug
```

The repository does not include the Gradle wrapper JAR. Use an installed Gradle
8.13 or restore the matching wrapper JAR before invoking `./gradlew`.
The GitHub Actions workflow runs these checks and uploads the debug APK.

Tests cover matching, persistence, adding/removing senders, an enabled empty
list, confirmation before allowing all senders, activity recreation, and pending
messages from removed senders. On a physical phone, additionally verify one SMS
from an allowed sender and one from an unrelated sender, then repeat after
restarting the app and retrying an offline delivery.

The debug APK is signed with a development key and cannot update the Play Store
installation in place. Before granting SMS permission, configure your allowlist
using the three-dot menu. Configure your own Telegram token and chat; no account
credentials are included in the APK.
