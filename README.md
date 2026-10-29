# 🛡️ منصة وتطبيق تطعيماتي (TateamtyApp)
### مشروع البنية التحتية للصحة الرقمية لرعاية الأمومة والطفولة

![Android Studio](https://img.shields.io/badge/Android%20Studio-2026.1-green?style=flat&logo=androidstudio)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue?style=flat&logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-M3-purple?style=flat&logo=android)
![Firebase](https://img.shields.io/badge/Firebase-Firestore-orange?style=flat&logo=firebase)

---

## 👩‍💻 إعداد وتطوير
* **المهندسة / الباحثة**: أميرة إدريس محمد
* **المؤسسة الأكاديمية**: جامعة السودان المفتوحة - كلية علوم الحاسوب وتقانة المعلومات
* **الدرجة العلمية**: بكالوريوس تقنية المعلومات (Bachelor of Information Technology)

---

## 📌 نبذة عن المنظومة
**منصة تطعيماتي (TateamtyApp)** هي منصة سحابية وتطبيق هاتف ذكي متكامل يُعنى بحوسبة وإدارة سجلات تطعيمات الأطفال، وتوفير منظومة توعوية واستباقية لأولياء الأمور والكوادر الطبية. يهدف المشروع إلى قيادة التحول الرقمي في قطاع رعاية الأمومة والطفولة عبر استبدال الكروت الورقية التقليدية بنظام سحابي ذكي ومؤمن بالكامل.

---

## ⚙️ أهم ميزات ووظائف النظام

### 1️⃣ بوابة الكادر الطبي (Medical Staff Portal)
* **تسجيل المواليد الجدد**: إدخال البيانات وحساب خوارزمية جدول الجرعات الـ 8 الأساسية تلقائياً.
* **البحث والتحديث الفوري**: الاستعلام بالرقم الوطني وتوثيق الجرعات المعطاة بالتاريخ واسم المركز الطبي.
* **إصدار الشهادات الرقمية (PDF)**: فحص آلي وتوليد شهادة إتمام التطعيم المعتمدة رسمياً بصيغة PDF.
* **التقارير والتحليلات البيانية**: متابعة معدلات الإكمال ومستوى التغطية اللقاحية للمجتمع.

### 2️⃣ بوابة ولي الأمر (Parent Portal)
* **تسجيل دخول سلس**: دخول مباشر وآمن برقم الهاتف المسجل دون الحاجة لكلمات سر معقدة.
* **متابعة إكمال التطعيمات**: شريط نسبة مئوية (Progress Bar) يعكس تقدم تحصين الأطفال بالأسرة.
* **دليل التوعية الصحية التفاعلي**: استعراض مفصل للقاحات، والأمراض الـ 5 المرقّمة (لخماسي)، ونصائح ما بعد التطعيم.

### 3️⃣ المحرك التلقائي والنظام الخلفي (Automation & Backend)
* **الجدولة الخوارزمية الذكية**: حساب مواعيد الجرعات الوطنية المعتمدة بدقة تمنع الخطأ البشري.
* **التنبيهات الاستباقية (WorkManager)**: إرسال إشعارات تذكيرية لهاتف ولي الأمر قبل الموعد بـ 24 ساعة.
* **المزامنة السحابية (Firebase Firestore)**: حفظ واسترجاع السجلات الطبية بمرونة وأمان مدى الحياة.

---

## 🛠️ التقنيات والمُعاملات البرمجية (Tech Stack)
* **Language**: Kotlin
* **UI Framework**: Jetpack Compose with Material Design 3
* **Backend Database**: Google Firebase Firestore
* **Background Processing**: Android WorkManager
* **Document Processing**: Android PdfDocument API
* **Architecture**: MVVM Architecture (Model-View-ViewModel)

---

## 🚀 كيفية تشغيل المشروع على Android Studio

1. قم بعمل `Clone` للمستودع عبر الأمر التالي في الـ Terminal:
   ```bash
   git clone https://github.com/Eng-amira2026/TateamtyApp-.git
   ```
2. افتح المجلد المُنزل باستخدام برنامج **Android Studio**.
3. قم بعمل `Sync Project with Gradle Files`.
4. اضغط على زر **Run** `Shift + F10` لتشغيل التطبيق على المحاكي (Emulator) أو هاتف ذكي مرتبط.

---

<p center="align">
  <b>حقوق الطبع والنشر والتطوير محفوظة للمهندسة أميرة إدريس محمد © 2026</b>
</p>
