# Student Remainder App

A robust and user-friendly Android application designed to help students organize their academic life, manage assignments, and stay on top of deadlines with a multi-layered reminder system.

## 🚀 Key Features

*   **Secure Authentication**: Personal accounts for every student with isolated data.
*   **Smart Reminders**: Automatic alerts 3 days, 2 days, and 1 day before a task is due.
*   **Multi-language Support**: Seamless switching between **English** and **Kiswahili**.
*   **Dark Mode**: Optimized dark theme for late-night study sessions.
*   **Task Management**: Create, edit, and track tasks with priority levels (Low, Medium, High).
*   **Visual Progress**: Dashboard with real-time progress bars and task filtering.
*   **Customizable Alerts**: User-selectable notification sounds from device storage.
*   **Privacy First**: Automatic logout when the app is closed (swiped away) to keep your data safe.

---

## 📂 Project Structure & File Descriptions

### 🏗️ Core Architecture
*   **`BaseActivity.java`**: The foundational class for all activities. it handles the global application of **Language** and **Theme** settings before any UI is rendered.
*   **`SessionManager.java`**: Manages user sessions using `SharedPreferences`. It stores login states, user details (ID, Name, Email), language preferences, and dark mode toggles.
*   **`DatabaseHelper.java`**: Handles all SQLite database operations. It manages two main tables: `users` and `tasks`, ensuring data persistence and multi-user isolation using User IDs.

### 📱 Activities (UI Logic)
*   **`SplashActivity.java`**: The entry point. Displays the app logo for 2 seconds while initializing core configurations.
*   **`LoginActivity.java`**: Handles user sign-in. It verifies credentials against the database and starts the session.
*   **`RegisterActivity.java`**: Allows new users to create accounts.
*   **`MainActivity.java`**: The main dashboard. Displays the task list, calculates completion percentages, and provides navigation to other features.
*   **`AddEditTaskActivity.java`**: A dynamic screen for creating new tasks or modifying existing ones, including priority and deadline settings.
*   **`CalendarActivity.java`**: Provides a calendar-view of tasks, helping users plan their week visually.
*   **`SettingsActivity.java`**: Central hub for app customization (Language, Dark Mode, Notifications).
*   **`ProfileActivity.java`**: Displays user details and allows for account logout.
*   **`EditProfileActivity.java`**: Enables users to update their personal information and choose a custom profile picture.
*   **`NotificationSettingsActivity.java`**: dedicated screen to toggle alerts and pick custom notification sounds.
*   **`AboutActivity.java` & `HelpSupportActivity.java`**: Provide app information, FAQs, and developer contact options.

### 🛠️ Helpers & Services
*   **`AlarmHelper.java`**: The logic engine for reminders. It calculates and schedules multiple system alarms for every task.
*   **`NotificationHelper.java`**: Handles the creation of notification channels and the delivery of system-tray alerts.
*   **`ThemeUtils.java`**: A utility to apply night mode settings programmatically.
*   **`LocaleHelper.java`**: Manages the runtime change of languages (English/Swahili).
*   **`AppTerminatorService.java`**: A background service that detects when the app is closed and triggers an automatic logout.
*   **`ReminderReceiver.java` & `BootReceiver.java`**: Broadcast receivers that listen for scheduled alarms and device reboots to ensure reminders are never missed.

### 🎨 Resources
*   **`res/values/strings.xml`**: Centralized English text resources.
*   **`res/values-sw/strings.xml`**: Full Swahili translations for the entire app.
*   **`res/values-night/colors.xml`**: Customized color palette for Dark Mode.
*   **`drawable/image.jpeg`**: The official app logo used in icons and branding.

---

## 🛠️ Tech Stack
*   **Language**: Java
*   **UI Framework**: Android XML / Material Design 3
*   **Database**: SQLite
*   **Storage**: SharedPreferences (Session management)
*   **Background Tasks**: AlarmManager, Broadcast Receivers, Services

---

## 🏁 Getting Started
1. Clone the repository.
2. Open the project in **Android Studio**.
3. Sync project with Gradle files.
4. Run the app on an emulator or physical device (API 26+ recommended).

---

## 👨‍💻 Developer
**Suhayb Coder**  
© 2026 Student Remainder App
