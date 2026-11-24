# Kora Challenge (Android)

Kotlin + Jetpack Compose. نسخة native من موقع كوره وتحديات.

## البناء من الموبايل
1. ارفع محتوى المجلد ده على repo جديد في GitHub (الـ .github لازم يتسجل كمان).
2. تاب Actions ← Build APK ← Run workflow (أو أي push بيشغله لوحده).
3. افتح آخر تشغيل ← Artifacts ← نزّل kora-challenge-debug-apk وفك الضغط ثبّت الـ APK.

## مكان التعديل
- الألوان: ui/theme/Color.kt
- قايمة الألعاب ونصوصها: data/Games.kt
- الشاشة الرئيسية: ui/home/HomeScreen.kt
- التنقل: navigation/AppNav.kt

## المراحل
1. هيكل + الرئيسية + بناء APK  ← إنت هنا
2. شاشات الألعاب  ← شغال: تحدي اليوم + تحدي المعلومات (ضد الكمبيوتر)
3. تسجيل الدخول (Firebase) + النقاط + الترتيب
4. الأونلاين (رسم اللاعبين / تحدي المعلومات / شات)
5. كارير مود ومدرب
6. أيقونة ونسخة نهائية
