# IING STD CYBER WIDGET — Android asli

Widget 4×4 hitam-hijau neon, bahasa Indonesia, baterai, suhu baterai, penyimpanan, dan status jaringan. Tidak membutuhkan root atau Termux untuk menjalankan widget setelah terpasang.

## Membuat APK
1. Ekstrak ZIP ini di komputer dengan Android Studio.
2. Buka folder `IING-STD-Cyber-Widget` sebagai proyek Android Studio. Tunggu Gradle Sync dan instalasi Android SDK yang diminta.
3. Pilih **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
4. APK debug ada di `app/build/outputs/apk/debug/app-debug.apk`. Salin ke HP dan instal.
5. Buka aplikasi sekali untuk memberi izin informasi jaringan. Tekan lama layar utama → Widget → IING STD Cyber Widget → tambahkan widget 4×4.

## Catatan batasan
- Widget Android memperbarui otomatis kira-kira setiap 30 menit (Android dapat menunda); sentuh teks pembaruan untuk memperbarui manual.
- Ketika Wi-Fi aktif, status jaringan menampilkan Wi-Fi, bukan teknologi seluler di belakangnya.
- 5G NSA dapat dilaporkan Android sebagai LTE. Jangan menganggap LTE selalu berarti tidak ada 5G.
- Tidak ada APK di ZIP ini: berisi kode sumber lengkap yang harus dikompilasi. Belum diuji di perangkat fisik.
