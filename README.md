# Amar Money Bag — Version 1.0.190826

Lightweight offline personal money-management app for Android.

## Performance choices
- Transactions are never observed as one large list during normal UI use.
- Journal and monthly summary load 40 records per page.
- Account balances and monthly totals are calculated by SQLite aggregate queries.
- Transaction indexes support account/date queries.
- The pager keeps no extra page beyond the current page.
- No INTERNET or network-state permission is declared.
- Startup uses the Android starting window with the app logo; there is no artificial splash delay or animation.
- The app targets Android 35 and supports Android 8.0 (API 26) and newer.

## Version
- versionName: `1.0.190826`
- versionCode: `190826`

Open this folder in Android Studio and build the APK with Gradle.
