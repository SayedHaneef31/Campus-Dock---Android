# 📱 CampusDock — The One-Stop App for College Life

**CampusDock** is a modular, scalable Android application designed to simplify and enhance the daily lives of college students. It serves as a centralized digital platform for essential campus services — including canteen food ordering, anonymous chatrooms, marketplace listings, attendance tracking, announcements, and more.

Built with **Android (Kotlin/XML)** and powered by a secure **Spring Boot** backend, CampusDock focuses on real-world utility, clean design, and seamless user experience across all modules.

---

## 🚀 Features

### ✅ Core Modules
- **🍔 Canteen Ordering**: Browse menus, place pickup-only orders, and get real-time operational status of campus canteens.
- **💬 Anonymous Chatrooms**: Topic-based open forums where students can engage freely and anonymously.
- **🛒 Campus Marketplace**: Post and explore items for sale — from books to electronics — within your college.
- **📊 Attendance Tracker**: Get a quick overview of attendance stats and upcoming lectures.
- **📣 College Announcements**: Admins can broadcast alerts and announcements to their college students.

### 🔒 Authentication & Security
- Email-based OTP verification (only college domain emails are allowed)
- JWT-based secured sessions
- Role-based access (Student, Admin, SuperAdmin)

### 🔧 Infrastructure & Tech
- **Backend**: Spring Boot + PostgreSQL + Redis (for future caching & real-time features)
- **Frontend**: Kotlin (Android) using ConstraintLayout and Navigation Components
- **Image Hosting**: Google Cloud / AWS S3 bucket
- **Email Delivery**: Resend API with verified domain (DMARC, DKIM, SPF secured)
- **Build Tools**: Gradle + ViewBinding + Retrofit + Glide

