<div align="center">

  <img src="desktop/public/icon.png" alt="Expense Tracker Logo" width="100" height="100" />

  # Expense Tracker 📱💰💻

  **A modern, private, offline-first personal finance and expense tracking app for Android & Desktop.**  
  *Take complete control of your finances without sacrificing privacy or relying on third-party cloud servers.*

  <br/>

  <!-- Active CTAs -->
  <p align="center">
    <a href="https://play.google.com/store/apps/details?id=com.neosparkx.expensetracker" target="_blank">
      <img src="assets/google-play-badge.png" 
           alt="Get it on Google Play" 
           height="54"
           align="middle" />
    </a>
    &nbsp;&nbsp;&nbsp;&nbsp;
    <a href="https://neosparkx.com/products/expense-tracker" target="_blank">
      <img src="assets/download-desktop.png" 
           alt="Download for Desktop Setup (v3.0.0)" 
           height="54"
           align="middle" />
    </a>
  </p>

  <!-- Status Badges -->
  [![Google Play](https://img.shields.io/badge/Google_Play-Available_Now-34A853?style=for-the-badge&logo=google-play&logoColor=white)](https://play.google.com/store/apps/details?id=com.neosparkx.expensetracker)
  [![Desktop App](https://img.shields.io/badge/Desktop_App-Download_Now-0078D4?style=for-the-badge&logo=windows&logoColor=white)](https://neosparkx.com/products/expense-tracker)
  [![Latest Release](https://img.shields.io/badge/Release-v3.0.0-blue?style=for-the-badge&logo=android)](https://github.com/nabil24024004/expense-tracker/releases)
  [![PRs Welcome](https://img.shields.io/badge/PRs-Welcome-brightgreen?style=for-the-badge&logo=github)](CONTRIBUTING.md)
  [![License](https://img.shields.io/badge/License-Apache%202.0-orange?style=for-the-badge)](LICENSE)

  <br/>

  <!-- Tech Badges -->
  [![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
  [![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=flat-square&logo=android)](https://developer.android.com/jetpack/compose)
  [![Room Database](https://img.shields.io/badge/Room%20DB-v2.7.0-4caf50?style=flat-square&logo=sqlite&logoColor=white)](https://developer.android.com/training/data-storage/room)
  [![Electron](https://img.shields.io/badge/Desktop-Electron_%2B_React_19-47848F?style=flat-square&logo=electron&logoColor=white)](https://www.electronjs.org/)
  [![Biometric](https://img.shields.io/badge/Security-Biometric%20Auth-darkgreen?style=flat-square&logo=lock)](https://developer.android.com/training/sign-in/biometrics)
  [![Min SDK](https://img.shields.io/badge/Min%20SDK-26%20(Android%208.0)-informational?style=flat-square&logo=android)](https://developer.android.com)

  <br/>

  [**📱 Google Play**](https://play.google.com/store/apps/details?id=com.neosparkx.expensetracker) •
  [**💻 Desktop App**](https://neosparkx.com/products/expense-tracker) •
  [**📦 APK Releases**](https://github.com/nabil24024004/expense-tracker/releases) •
  [**Report Bug**](https://github.com/nabil24024004/expense-tracker/issues/new?template=bug_report.md) •
  [**Request Feature**](https://github.com/nabil24024004/expense-tracker/issues/new?template=feature_request.md) •
  [**Collaborate**](#-open-for-collaboration--contributing)

</div>

---

## 📖 About Expense Tracker

Most personal finance apps require cloud sync, expose banking logins to third-party brokers, clutter screens with ads, or lock basic features behind subscriptions. 

**Expense Tracker** was built on a different principle: **Your financial data belongs exclusively to you.** 

It runs **100% locally on your device**, without tracking, external telemetry, or cloud databases. Combining a fluid **Jetpack Compose (Material 3)** mobile experience with a dedicated **Electron & React 19** desktop client and an offline **Room DB** engine, Expense Tracker provides deep budgeting tools, multi-wallet accounts, smart math evaluation, debt ledgers, recurring payment automation, and military-grade hardware biometric locking.

---

## 📥 Download & Available Platforms

Choose your preferred platform to start tracking your finances offline:

| Platform | Channel / Download | Format | Description |
| :--- | :--- | :--- | :--- |
| **Android (Mobile)** | <a href="https://play.google.com/store/apps/details?id=com.neosparkx.expensetracker" target="_blank"><img src="assets/google-play-badge.png" height="38" alt="Google Play" /></a> | Store (`.aab`) | Official Google Play release with automatic in-app updates. Requires Android 8.0+. |
| **Windows (Desktop)** | <a href="https://neosparkx.com/products/expense-tracker" target="_blank"><img src="assets/download-desktop.png" height="38" alt="Download Desktop Setup" /></a> | `.exe` (Installer & Portable) | Full-featured desktop workspace with large-screen charts, instant Excel/JSON sync, and zero cloud dependency. |
| **Android (Direct APK)** | [![GitHub Releases](https://img.shields.io/badge/GitHub_Releases-Download_APK-blue?style=for-the-badge&logo=github)](https://github.com/nabil24024004/expense-tracker/releases) | `.apk` (Direct) | Direct sideload release for de-Googled devices or offline installations. |
| **macOS & Linux** | *Coming Soon* | `.dmg` / `.AppImage` | Under active packaging. Join the [collaboration](#-open-for-collaboration--contributing) to help test! |

---

## ⚡ Highlights at a Glance

| Feature | Description |
| :--- | :--- |
| 🛡️ **100% Offline & Private** | Zero telemetry, zero cloud tracking, zero accounts required. All logs stay safely stored in your device's local database. |
| 🧮 **Inline Math Calculator** | Type arithmetic equations (`150 + 45 * 1.05`) directly into the amount field with real-time evaluation preview. |
| 💳 **Multi-Account Wallets** | Manage Cash, Bank Accounts, Credit Cards, and Savings with custom colors, bank names, and inter-wallet transfers. |
| 🔄 **Recurring & Auto-Deposit** | Automate routine bills and subscriptions with overdue alerts. Automatically credit salary/income deposits on due dates. |
| 🤝 **Debts & Receivables** | Track who owes you money and who you owe, with due dates, partial repayments, and direct ledger settlement. |
| 📊 **Dynamic Spending Insights** | Track remaining budgets across Weekly, Monthly, or Custom payroll cycles with smooth 7-day spending trendlines. |
| 🔒 **Biometric & Privacy Shield** | Lock sensitive records behind fingerprint/face authentication. Tap the privacy eye to blur balances (`••••`) in public. |
| 📁 **Full Data Ownership** | Export your transactions to Excel (`.xlsx`), native PDF summary statements, CSV, or restore full backups via JSON. |

---

## 🌟 Core Features Walkthrough

### 1. 📊 Smart Dashboard & Budget Controller
* **Remaining Budget Tracker**: High-visibility progress bar showing active spending against your budget limit. Supports **Monthly**, **Weekly**, or **Custom Payroll Cycles** (e.g. 15th to 15th).
* **Interactive Trend Charts**: Smooth 7-day spending line graphs powered by Vico (mobile) and Recharts (desktop) to visualize cash outflows.
* **Quick Spend Hub**: Horizontal category shortcuts (Food, Transport, Bills, Shopping, etc.) to log everyday purchases in seconds.
* **Smart Alert Cards**: Instant notices for budget overruns, upcoming bills, and healthy spending habits.

### 2. 💳 Multi-Account & Wallet System
* **Custom Financial Pools**: Create dedicated cards representing Cash, Checking, Credit Cards, or High-Yield Savings.
* **Personalized Visual Cards**: Choose custom hex colors, card types, bank names, and initial balances.
* **Inter-Account Transfers**: Move funds between wallets (e.g., ATM cash withdrawals or card payments) with automatic balance reconciliation.
* **Net Balance Exclusions**: Exclude specific accounts (e.g., locked investment portfolios) from your main dashboard balance.

### 3. 🧮 Smart Transaction Logging & Inline Math
* **Arithmetic Input Parser**: Evaluate math inline (`2500 / 3 + 120`) without switching back and forth to an external calculator.
* **Category & Tagging**: Organize spending with expressive icons, colors, and multiple searchable tags.
* **Transaction Types**: Clear classification of **Expense** (deducts from wallet) or **Income** (credits wallet).

### 4. 🔄 Recurring Subscriptions & Planned Expenses
* **Flexible Schedules**: Automate recurring expenses on Daily, Weekly, Monthly, or Yearly intervals, or schedule one-off future payments.
* **Overdue Highlights**: Automatically flags missed or upcoming due dates in bold red on your dashboard.
* **Automated Income Deposits**: Scheduled earnings (such as monthly salaries) auto-credit to your selected wallet in the background on their due date.
* **Pay / Skip Workflow**: Manually approve expense payments to log them into the active ledger, or skip a cycle without modifying account balances.

### 5. 🤝 Debts & Receivables Ledger
* **Borrowing & Lending Records**: Track debts (what you owe) and dues (what others owe you) linked to people with optional due dates.
* **Integrated Ledger Settlement**: Log repayments with a single toggle that automatically registers corresponding income/expense entries in your wallet.

### 6. 🔍 Ledger History & Advanced Filters
* **Instant Tag & Text Search**: Filter records by description keywords, category, or custom tag chips.
* **Interval Grouping**: Group historical logs by Week, Month, or Year with calculated subtotals.
* **Calendar Range Selectors**: Inspect custom historical date intervals with ease.

### 7. 🛡️ Privacy, Security & Portability
* **Public Privacy Masking**: Toggle Privacy Mode to blur balances and income values (`••••`) on the fly when in crowded spaces.
* **Biometric Lock**: Protect your financial data with Android's hardware-backed BiometricPrompt (fingerprint or facial unlock).
* **Zero Cloud Lock-in**: Export data anytime to **Excel (`.xlsx`)**, formatted **PDF Reports**, or **CSV**, and backup/restore complete databases with **JSON**.

---

## 💡 Best Practices for Maximum Efficiency

1. **Map Out All Real-Life Wallets**: Set up distinct accounts for physical cash, payroll checking, credit cards, and savings so your net worth is always accurate.
2. **Align Budgets with Your Payday**: If your paycheck lands on the 1st, 15th, or every other Friday, configure a **Custom Budget Period** so remaining funds mirror your actual cash cycle.
3. **Use the Math Evaluator for Bill Splits**: Split dinner or group bills directly in the amount field (`4800 / 4 + 150`) to save time.
4. **Automate Fixed Overhead**: Schedule subscriptions (Netflix, Spotify, rent) under Planned Transactions so you never miss a due date.
5. **Enable Public Privacy Mode**: Keep Privacy Mode accessible when you're logging transactions in cafes or public transit.
6. **Cross-Platform Offline Backups**: Export your backup file via JSON or Excel on your phone and import it directly into the Desktop app for big-screen financial reviews!

---

## 🛠️ Technical Architecture

Expense Tracker is structured cleanly across both its Android mobile and desktop clients:

### 📱 Android Mobile Architecture
```
com.neosparkx.expensetracker/
├── data/
│   ├── entity/        # Room Database Entities (Expense, Account, DebtDue, Planned)
│   ├── dao/           # Reactive Database Access Objects (Room Flows)
│   └── repository/    # Local Data Repositories & Business Logic
├── ui/
│   ├── components/    # Reusable Material 3 Compose UI Components
│   ├── screens/       # Screen Composables (Dashboard, Wallets, Debts, History, Settings)
│   └── theme/         # Color palettes, typography, shapes, and dynamic theming
├── viewmodel/         # ViewModels exposing StateFlow to Composables
└── utils/             # Math parser, biometric manager, POI Excel/PDF exporter
```

### 💻 Desktop Client Architecture
```
desktop/
├── electron/          # Main process lifecycle, window configuration & IPC bridges
├── src/
│   ├── components/    # React 19 UI components styled with Tailwind CSS
│   ├── assets/        # Visual icons and graphic resources
│   └── utils/         # Local file readers, XLSX spreadsheets, and jsPDF exporters
└── vite.config.ts     # Vite fast-bundler and React plugin configuration
```

| Layer | Android App | Desktop App |
| :--- | :--- | :--- |
| **Language** | Kotlin 2.2.10 | TypeScript 6.0 / JavaScript |
| **UI Framework** | Jetpack Compose (Material 3) | React 19 + Tailwind CSS |
| **Runtime / Container** | Native Android (Min SDK 26, Target 36) | Electron 43 |
| **State / Reactivity** | StateFlow & Coroutines | React Hooks & State |
| **Local Persistence** | Room DB v2.7.0 (SQLite) | Local Storage / IndexedDB & File Storage |
| **Charts** | Vico Charts | Recharts |
| **Documents** | Apache POI & Android `PdfDocument` | XLSX & jsPDF |
| **Security** | AndroidX Biometric (Hardware Keystore) | Local OS Sandbox |

---

## 🚀 Getting Started

### 📱 Running the Android Mobile App
1. **Prerequisites**: [Android Studio](https://developer.android.com/studio) (Ladybug or newer), JDK 17+, Android SDK 26+.
2. **Clone & Open**:
   ```bash
   git clone https://github.com/nabil24024004/expense-tracker.git
   cd expense-tracker
   ```
3. **Build & Run**:
   - Open the root folder in Android Studio.
   - Sync Gradle and click the green **Run (▶)** button, or execute:
     ```bash
     ./gradlew assembleDebug
     ```

### 💻 Running the Desktop App
1. **Prerequisites**: Node.js 20+ and npm.
2. **Navigate & Install**:
   ```bash
   cd desktop
   npm install
   ```
3. **Run Dev Mode (Vite + Electron)**:
   ```bash
   npm run dev
   ```
4. **Build Desktop Executable**:
   ```bash
   npm run electron:build
   ```

---

## 🤝 Open for Collaboration & Contributing

We welcome and celebrate contributions from developers, designers, writers, and finance enthusiasts around the world! Whether you are an Android Kotlin expert, a frontend React/Electron developer, or a UI/UX designer, **your help is warmly appreciated.**

### 🌟 Areas Where You Can Contribute
* **📱 Android Improvements**:
  - **On-Device OCR Receipt Scanning** using Google ML Kit
  - **Interactive Home Screen & Lock Screen Widgets** via Jetpack Glance
  - **Predictive Spending Insights** and anomaly warnings
* **💻 Desktop App (Electron & React 19)**:
  - Multi-window dashboards, advanced chart filtering, and keyboard shortcut power tools
  - Native packaging for **macOS (`.dmg`)** and **Linux (`.AppImage`, `.deb`)**
* **🎨 UI/UX & Design**: 
  - Material You dynamic theming and color palettes
  - Micro-interactions, spring animations, and refined layout transitions
  - Tablet and foldable responsive layouts
* **🌍 Localization & Translations**:
  - Help translate strings into Spanish, French, German, Arabic, Hindi, Bengali, Japanese, Portuguese, and other languages.
* **🧪 Testing & Quality Assurance**:
  - Compose UI tests, Robolectric unit tests, and performance profiling on low-end devices.
* **📖 Documentation & Guides**:
  - Tutorials, architecture diagrams, user guides, or walkthrough videos.

### 🛠️ How to Contribute Step-by-Step
1. **Explore Existing Issues**: Check our [Issues](https://github.com/nabil24024004/expense-tracker/issues) tab for open bugs, requested features, or `good-first-issue` tags.
2. **Fork the Repository**: Click **Fork** at the top right of this page and clone your fork locally.
3. **Create a Feature Branch**:
   ```bash
   git checkout -b feature/your-awesome-feature
   ```
4. **Write Clean Code**: Follow standard coding guidelines and ensure your code compiles cleanly.
5. **Test Your Changes**: Verify that the app builds and tests pass:
   ```bash
   ./gradlew test        # For Android
   npm run lint          # For Desktop
   ```
6. **Commit & Push**:
   ```bash
   git commit -m "feat: add receipt scanning with ML Kit"
   git push origin feature/your-awesome-feature
   ```
7. **Submit a Pull Request**: Open a PR against the `main` branch with a clear description of your changes.

> 💡 **Have a feature idea or question?**  
> Don't hesitate to open a [Discussion](https://github.com/nabil24024004/expense-tracker/discussions) or submit a [Feature Request](https://github.com/nabil24024004/expense-tracker/issues/new). Let's build the best privacy-centric finance manager together!

Read our full [Contributing Guidelines](CONTRIBUTING.md) for more details.

---

## 🗺️ Product Roadmap

- [x] Multi-wallet system with inter-account transfers
- [x] Inline math calculator keyboard
- [x] Biometric security & public privacy masking mode
- [x] Excel (`.xlsx`), PDF, CSV, and JSON export/import
- [x] Recurring subscriptions and automated salary deposits
- [x] Dedicated Windows Desktop client (Installer & Portable)
- [ ] **Jetpack Glance Home Screen Widgets** (quick-add transaction, budget glance)
- [ ] **On-Device OCR Receipt Scanner** with Google ML Kit
- [ ] **macOS and Linux Desktop builds**
- [ ] **Multi-currency support** with offline conversion cache
- [ ] **Opt-in Encrypted Backup** to user-owned WebDAV or Google Drive

---

## 🛡️ Privacy Policy

Expense Tracker is completely offline and adheres to a strict zero-data-collection philosophy. No personal information, financial records, or device identifiers are collected, tracked, or transmitted. 

Review the complete [Privacy Policy](PRIVACY_POLICY.md) for details.

---

## 📄 License

This project is open-source software licensed under the **Apache License 2.0**.  
See the [LICENSE](LICENSE) file for terms and conditions.

---

<div align="center">

  **Crafted with care by NeoSparkX**  
  *If you find Expense Tracker helpful, give it a ⭐️ on GitHub, check out the [Desktop App](https://neosparkx.com/products/expense-tracker), and leave a review on the [Google Play Store](https://play.google.com/store/apps/details?id=com.neosparkx.expensetracker)!*

</div>
