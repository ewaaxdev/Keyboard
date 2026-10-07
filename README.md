# Keyboard

Keyboard Android super ringan gaya iPhone: QWERTY, halaman angka/simbol
(123 dan #+=), shift satu ketuk, hapus-tahan. Tanpa saran kata,
tanpa kamus, tanpa internet, tanpa library pihak ketiga.

## Cara dapat APK

Buka tab **Actions** di GitHub, pilih run terbaru, unduh artifact
**app-debug**, install di HP (min. Android 7.0).

## Cara mengaktifkan

1. Buka aplikasi Keyboard.
2. Tekan "Buka pengaturan keyboard", aktifkan Keyboard.
3. Tekan "Pilih keyboard", pilih Keyboard.

## Build lokal (opsional)

Butuh Android Studio + JDK 17, lalu jalankan `./gradlew assembleDebug`.
APK ada di `app/build/outputs/apk/debug/app-debug.apk`.

## Isi project

- `app/src/main/java/.../SimpleKeyboardService.kt` — otak keyboard.
- `app/src/main/res/layout/keyboard_view.xml` — tampilan tombol.
- `app/src/main/java/.../SettingsActivity.kt` — layar 2 tombol pengaturan.
- `app/src/main/AndroidManifest.xml` — pendaftaran ke sistem.
- `.github/workflows/build.yml` — rakit APK otomatis tiap push.
