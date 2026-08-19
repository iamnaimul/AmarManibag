# Amar Money Bag

**Version:** 1.0.190826\
**Application ID:** `com.iamnaimul.amarmanibag`\
**Platform:** Android\
**Minimum Android Version:** Android 8.0 (API 26)\
**Target Android Version:** Android 15 (API 35)\
**Developer:** Naimul Hassan

> **Amar Money Bag** is a lightweight, offline-first personal money
> management application for Android. It is designed for simple personal
> income and expense tracking, with a Bengali user interface and local
> data storage.

## Overview

Amar Money Bag helps users record and manage personal financial
transactions without requiring an internet connection or an online
account.

The application is intentionally designed to be lightweight and suitable
for lower-spec Android phones. Normal app usage does not load the entire
transaction database into memory at once. Transactions are loaded in
small pages, while totals and account balances are calculated directly
by the local SQLite/Room database.

## Main Features

### Dashboard / Home

The Home screen provides a quick overview of the user's finances:

-   Total current balance
-   Current month's total income
-   Current month's total expense
-   Account/source-wise balances
-   Quick access to add a new transaction
-   Scrollable account/source cards when there are more accounts than
    can fit on screen

The Home screen does **not** load or display a complete
recent-transaction list.

### Add Transactions

New transactions can be recorded directly from the Home screen.

A transaction can contain:

-   Date
-   Money source/account
-   Transaction type
    -   Income
    -   Expense
-   Description
-   Amount

### Journal

The Journal contains the complete transaction history.

To reduce memory usage:

-   Transactions are loaded in pages rather than all at once.
-   The current implementation uses a page size of 40 records.
-   Additional records are loaded as the user scrolls.
-   Search and filters are processed through database queries.
-   The UI uses lazy scrolling so only visible items need to be
    composed.

Available filtering includes transaction type, account/source and date
range, along with text search.

### Monthly Summary

The Summary screen is focused on transactions for the selected month.

It provides:

-   Selected month
-   Number of transactions in that month
-   Monthly transaction list
-   Month navigation
-   Paginated loading of monthly transactions

The account/source dashboard is intentionally kept on the Home screen
rather than duplicated in Summary.

### Money Sources / Accounts

Users can maintain multiple money sources/accounts, such as:

-   Cash
-   Bank account
-   Mobile financial service
-   Other personal sources

Accounts can be activated or deactivated, and each account can have an
opening balance.

Account balances are calculated using database-level aggregation rather
than loading all related transactions into application memory.

### Backup and Restore

The application supports local backup and restore.

The intended backup folder structure is:

``` text
AmarMoneyBag/
└── BackUps/
```

Backup data is stored locally and can be restored after reinstalling the
application.

The application does not require cloud storage or an internet connection
for normal backup/restore operations.

### Theme

The application supports:

-   Light mode with an off-white paper-style background
-   Dark mode

The interface is designed primarily for practical daily use with a clean
and minimal layout.

### Navigation

The main sections are:

-   Home
-   Journal
-   Summary
-   Settings

Users can navigate using the bottom navigation bar and horizontal swipe
gestures.

The individual screens also support vertical scrolling where required.

## Performance and Resource Design

Amar Money Bag is designed with low-resource devices in mind.

### Database-first calculations

Monthly income, monthly expense, transaction counts and account balances
are calculated using SQLite/Room aggregate queries.

This avoids repeatedly loading and filtering large transaction
collections in Kotlin.

### Paginated transaction loading

The application does not normally read every transaction into memory.

Journal and monthly transaction screens use paginated database queries
with a page size of 40.

For example:

``` text
Database
   ↓
First 40 transactions
   ↓
User scrolls
   ↓
Next 40 transactions
   ↓
User scrolls
   ↓
Next 40 transactions
```

### Lazy UI

Transaction and account lists use lazy scrolling components so the UI
does not need to compose every list item simultaneously.

### Lightweight startup

The application does not use an artificial startup delay or a custom
splash Activity.

Instead, Android's native starting-window/splash mechanism is used to
display the application logo while the main activity starts.

The startup logo uses the application's wallet artwork with a 25%
rounded-square shape.

### No network access

The application is designed to work completely offline.

The Android manifest intentionally does not request:

-   `INTERNET`
-   Network state
-   Bluetooth
-   Other network-related permissions

## Data Storage

Application data is stored locally using:

-   SQLite
-   Android Room

The application does not require an external server or online database.

Transaction data includes fields such as:

-   Transaction ID
-   Account/source ID
-   Transaction date
-   Transaction type
-   Description
-   Amount
-   Creation/update information

## Privacy

Amar Money Bag is intended as a personal offline money-management
application.

Because normal operation does not require an internet connection:

-   Financial records remain on the device unless the user
    exports/backups them.
-   No online account is required.
-   No cloud service is required for normal operation.
-   No internet permission is declared by the application.

Users should still protect their phone and backup files because local
application data and backup files can contain sensitive financial
information.

## Bengali Interface

The user interface is designed primarily in Bengali.

The project includes Noto Sans Bengali font resources for Bengali text
rendering.

Amounts are intended to be displayed using English numerals for
practical financial entry and reading.

## Technology Stack

-   **Language:** Kotlin
-   **UI:** Jetpack Compose
-   **Architecture:** Android application with ViewModel + Repository
    pattern
-   **Database:** Room / SQLite
-   **Database queries:** SQL aggregate and paginated queries
-   **Build system:** Gradle with Kotlin DSL
-   **Dependency processing:** KSP
-   **Minimum SDK:** 26
-   **Target SDK:** 35
-   **Compile SDK:** 35

## Project Structure

The main source package is:

``` text
com.iamnaimul.amarmanibag
```

Important components include:

``` text
app/
└── src/main/
    ├── AndroidManifest.xml
    ├── java/com/iamnaimul/amarmanibag/
    │   ├── MainActivity.kt
    │   ├── AmarManibagApplication.kt
    │   ├── AppContainer.kt
    │   ├── BackupManager.kt
    │   ├── Theme.kt
    │   ├── Ui.kt
    │   ├── ViewModel.kt
    │   ├── data/
    │   │   ├── Account.kt
    │   │   ├── AppRepository.kt
    │   │   ├── Daos.kt
    │   │   ├── Settings.kt
    │   │   └── Transaction.kt
    │   └── ui/
    │       ├── App.kt
    │       ├── ReportScreen.kt
    │       ├── SettingsScreen.kt
    │       └── TransactionsScreen.kt
    └── res/
        ├── drawable/
        ├── drawable-nodpi/
        ├── font/
        ├── mipmap-*/
        └── values/
```

## Build

Open the project folder in Android Studio.

The project is configured with:

``` text
compileSdk = 35
targetSdk = 35
minSdk = 26
versionName = 1.0.190826
versionCode = 190826
```

Then use Android Studio's Gradle sync and build tools to generate the
APK.

## Version

### 1.0.190826

This release focuses on:

-   Lightweight operation
-   Reduced memory usage
-   Paginated transaction loading
-   Database-level aggregation
-   Faster startup
-   Native Android startup logo
-   25% rounded startup logo
-   Offline operation
-   Home dashboard simplification
-   Monthly transaction-focused Summary
-   Improved support for lower-spec Android devices

## Developer

**Naimul Hassan**

**Developed by Naimul Hassan**\
*A gift to RabbatuALBayt*

## License

This project is intended for personal use.

If you publish this repository publicly, add the license that reflects
how you want others to use, modify, and redistribute the project.

## Disclaimer

Amar Money Bag is a personal record-keeping tool. It is not a banking
application, accounting service, financial institution, or financial
advisory service.

Users are responsible for maintaining their own backups and verifying
financial records.
