<div align="center">

# 💰 আমার মানিব্যাগ

### ব্যক্তিগত আয়-ব্যয়ের সহজ ও অফলাইন হিসাবরক্ষণ অ্যাপ

<p>
  <strong>Amar Manibag</strong> is a simple, private and offline-first Android application for managing personal income and expenses.
</p>

<br>

[![Download APK](https://img.shields.io/badge/Download-Latest%20APK-brightgreen?style=for-the-badge\&logo=android)](../../releases/latest)
[![GitHub Release](https://img.shields.io/github/v/release/YOUR_USERNAME/AmarManibag?style=for-the-badge\&logo=github)](../../releases)
[![GitHub Repo](https://img.shields.io/badge/GitHub-Repository-black?style=for-the-badge\&logo=github)](.)

<br><br>

</div>

## 📖 About

**আমার মানিব্যাগ (Amar Manibag)** একটি সহজ, ব্যক্তিগত এবং সম্পূর্ণ অফলাইন Android application, যার মাধ্যমে দৈনন্দিন আয় ও ব্যয়ের হিসাব সহজে সংরক্ষণ ও পর্যবেক্ষণ করা যায়।

অ্যাপটির মূল লক্ষ্য হলো ব্যক্তিগত অর্থ ব্যবস্থাপনার জন্য একটি সহজ, দ্রুত, নির্ভরযোগ্য এবং internet-independent solution প্রদান করা।

এই অ্যাপটি ব্যক্তিগত ব্যবহারের কথা মাথায় রেখে তৈরি করা হয়েছে। ব্যবহারকারীর আর্থিক তথ্য কোনো online account বা cloud service-এর ওপর নির্ভর করে না।

---

## ✨ Features

* 💰 আয় ও ব্যয়ের হিসাব সংরক্ষণ
* 📊 বর্তমান ব্যালেন্স দেখা
* 📝 লেনদেনের বিস্তারিত ইতিহাস
* 📅 তারিখ অনুযায়ী লেনদেন সংরক্ষণ
* ✏️ পূর্বের transaction সম্পাদনা
* 🗑️ প্রয়োজন হলে transaction মুছে ফেলা
* 💾 Local SQLite database
* 🔄 Database backup and restore
* 📤 মাসিক হিসাব CSV হিসেবে export
* 🌐 সম্পূর্ণ Offline
* 🇧🇩 বাংলা user interface
* 🔢 আর্থিক পরিমাণ English numerals-এ প্রদর্শন
* 🔒 ব্যক্তিগত transaction data device-এ localভাবে সংরক্ষণ
* ⚡ দ্রুত এবং lightweight interface
* 📱 Android-এর জন্য optimized UI


## 🛠️ Built With

| Technology          | Purpose                      |
| ------------------- | ---------------------------- |
| **Kotlin**          | Application development      |
| **Jetpack Compose** | Modern Android UI            |
| **SQLite**          | Local data storage           |
| **Android Studio**  | Development environment      |
| **Android SDK**     | Android application platform |

---

## 🗃️ Data Storage

আমার মানিব্যাগ local SQLite database ব্যবহার করে transaction এবং application settings সংরক্ষণ করে।

প্রধান তথ্যগুলোর মধ্যে রয়েছে:

* Transaction date
* Transaction type
* Description
* Amount
* Created time
* Updated time
* Opening balance

ডেটা local device storage-এ রাখা হয় এবং অ্যাপের স্বাভাবিক ব্যবহারের জন্য internet connection প্রয়োজন হয় না।

---

## 💾 Backup & Restore

ব্যবহারকারীর গুরুত্বপূর্ণ financial data নিরাপদ রাখার জন্য application-এ database backup এবং restore সুবিধা রয়েছে।

### Backup

ব্যবহারকারী প্রয়োজন অনুযায়ী নিজের database backup সংরক্ষণ করতে পারবেন।

### Restore

অ্যাপ uninstall/reinstall করার পর পূর্বে তৈরি করা backup ব্যবহার করে database পুনরুদ্ধার করা সম্ভব।

> **Important:** Backup file নিরাপদ জায়গায় সংরক্ষণ করুন। Backup হারিয়ে গেলে সেই backup থেকে data restore করা সম্ভব হবে না।

---

## 📤 Monthly Export

মাসিক আয়-ব্যয়ের হিসাব সংরক্ষণ ও বিশ্লেষণের জন্য transaction data CSV format-এ export করা যায়।

CSV file Microsoft Excel, Google Sheets এবং অন্যান্য spreadsheet application-এ খোলা যায়।

---

## 🔐 Privacy

আমার মানিব্যাগ একটি **offline-first personal finance application**।

### Privacy principles

* কোনো account/login প্রয়োজন নেই।
* কোনো cloud account প্রয়োজন নেই।
* কোনো online server-এর ওপর transaction data নির্ভর করে না।
* Internet connection ছাড়া application-এর মূল হিসাবরক্ষণ কার্যক্রম ব্যবহার করা যায়।
* Personal transaction data local device storage-এ সংরক্ষণ করা হয়।
* Backup file ব্যবহারকারীর নিজের নিয়ন্ত্রণে থাকে।

**Your financial records belong to you.**

---

## 📥 Installation

### Option 1 — Download APK

সবচেয়ে সহজ উপায় হলো GitHub Releases থেকে সর্বশেষ APK ডাউনলোড করা।

1. এই repository-এর **Releases** page-এ যান।
2. সর্বশেষ release নির্বাচন করুন।
3. `AmarManibag-*.apk` ফাইলটি download করুন।
4. Android device-এ APK file-টি open করুন।
5. প্রয়োজন হলে Android-এর **Install unknown apps** permission চালু করুন।
6. Install করুন।
7. অ্যাপটি চালু করে ব্যবহার শুরু করুন।

### 📱 Download Latest APK

[![Download Latest APK](https://img.shields.io/badge/📱%20Download-Latest%20APK-brightgreen?style=for-the-badge)](../../releases/latest)

---

## 💻 Build From Source

আপনি চাইলে source code থেকে নিজেই application build করতে পারবেন।

### Clone the repository

```bash
git clone https://github.com/YOUR_USERNAME/AmarManibag.git
```

তারপর Android Studio-তে project directory open করুন।

Android Studio project sync সম্পন্ন করার পর:

**Build → Build Bundle(s) / APK(s) → Build APK(s)**

এর মাধ্যমে APK তৈরি করতে পারবেন।

---

## 📂 Project Structure

```text
AmarManibag/
│
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           ├── res/
│           └── AndroidManifest.xml
│
├── screenshots/
│   ├── home.png
│   ├── transactions.png
│   ├── add_transaction.png
│   └── settings.png
│
├── assets/
│   └── app-logo.png
│
├── gradle/
│
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
├── gradlew.bat
└── README.md
```

---

## 🚀 Releases

Application-এর নতুন version এবং APK GitHub Releases-এর মাধ্যমে প্রকাশ করা হবে।

### Current Version

**Version:** `1.0.1`

**Release:** First public release

[![View Releases](https://img.shields.io/badge/View-All%20Releases-blue?style=for-the-badge\&logo=github)](../../releases)

---

## 📝 Release History

### v1.0.1

**Initial Release**

* Personal income tracking
* Personal expense tracking
* Balance calculation
* Transaction history
* Local SQLite database
* Backup and restore
* CSV export
* Bengali user interface
* Offline operation

---

## 🧭 Roadmap

Future versions may include improvements such as:

* 📊 More detailed financial reports
* 📈 Monthly income/expense statistics
* 🔎 Improved transaction search
* 🏷️ Transaction categories
* 📅 Advanced date filtering
* 🎨 UI improvements
* 📊 Visual charts
* 🔐 Additional backup improvements

> Features listed in the roadmap are planned ideas and may change in future releases.

---

## ⚠️ Important Notes

* This application is primarily intended for personal use.
* Always keep a secure copy of your backup file.
* Do not delete your backup until you have confirmed that the restored data is correct.
* The APK distributed through GitHub Releases should preferably be a signed release build.
* Before installing a new version, keeping a recent backup is recommended.

---

## 🤝 Contributing

This project is primarily developed for personal use.

Suggestions, bug reports and improvements are welcome through the GitHub repository.

If you find a problem, please open an **Issue** and provide:

1. Application version
2. Android version
3. Device model
4. Description of the problem
5. Steps to reproduce the problem
6. Screenshot, if applicable

---

## 🐛 Bug Reports

If you discover a bug, please create a GitHub Issue.

### Please include:

```text
Application Version:
Android Version:
Device:
Problem:
Steps to Reproduce:
Expected Result:
Actual Result:
```

---

## 📄 License

This project is currently provided for personal use.

The licensing terms may be defined in a future release.

---

## 👨‍💻 Developer

**Md. Naimul Hassan**

Teacher & Developer

---

<div align="center">

### 💰 আমার মানিব্যাগ

**সহজ হিসাব • ব্যক্তিগত তথ্য • সম্পূর্ণ অফলাইন**

<br>

[📱 Download Latest APK](../../releases/latest)

<br><br>

Made with ❤️ for simple personal finance management.

</div>
