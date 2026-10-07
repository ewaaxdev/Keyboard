// Daftar versi plugin. AGP 9 sudah membawa Kotlin di dalamnya (built-in),
// jadi plugin Kotlin TIDAK dipasang terpisah (akan tabrakan).
plugins {
    id("com.android.application") version "9.0.1" apply false
}
