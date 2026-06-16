EcoScan AI - Aplikasi Klasifikasi Sampah Cerdas

EcoScan AI adalah aplikasi Android berbasis kecerdasan buatan (AI) yang dirancang untuk membantu masyarakat memilah sampah dengan lebih cerdas dan tepat. Dengan memanfaatkan model *Machine Learning* (TensorFlow Lite), aplikasi ini dapat mendeteksi berbagai jenis sampah secara *real-time* melalui kamera ponsel dan memberikan panduan daur ulang yang interaktif.

Tim Pengembang (Kelompok)
Proyek ini dikembangkan oleh mahasiswa Program Studi Sistem dan Teknologi Informasi, Fakultas Teknik, Universitas Negeri Jakarta:
1. **Fiqla Syabifi** - 1519624003
2. **Fadlan Nur Alifiansyah** - 1519624011
3. **Elevina Zukhruf** - 1519624015

Dosen Pembimbing: Murien Nugraheni, S.T., M.Cs.

Mata Kuliah: Kecerdasan Buatan Semester 124

Fitur Utama
1. **Pemindai Sampah Cerdas (AI Scanner):** Menggunakan kamera (CameraX) untuk mendeteksi jenis sampah (Plastik, Kertas, Kardus, Kaca, Logam, dll) dengan tingkat akurasi tinggi.
2. **Tips Daur Ulang Dinamis:** Menampilkan panduan dan langkah-langkah pengelolaan sampah yang menyesuaikan secara otomatis dengan hasil pindai AI.
3. **Riwayat Pemindaian (Local Database):** Menyimpan foto, jenis sampah, persentase keyakinan AI, dan tanggal pemindaian ke dalam basis data lokal (Room Database) yang bisa diakses kapan saja tanpa perlu memindai ulang.
4. **Cari Tempat Sampah Terdekat:** Integrasi cerdas dengan Google Maps untuk melacak lokasi Bank Sampah atau Tempat Pembuangan Sampah terdekat berdasarkan jenis limbah.

Tautan Unduhan (Download)
Aplikasi EcoScan AI (versi terbaru) dapat diunduh melalui tautan Google Drive resmi di bawah ini:
**[Download APK EcoScan AI di sini](https://drive.google.com/drive/folders/1TPodqSum4862JdRzYFEEah-8BfGy-CII?usp=sharing)**

Cara Instalasi (Panduan Pengguna)
Karena aplikasi ini belum dipublikasikan di Google Play Store, silakan ikuti langkah-langkah berikut untuk memasangnya di perangkat Android Anda:
1. Klik tautan *download* di atas melalui *browser* ponsel Android Anda.
2. Unduh file **`.apk`** yang tersedia di dalam folder tersebut.
3. Setelah unduhan selesai, buka file APK tersebut.
4. Jika muncul peringatan keamanan *"Instal aplikasi dari sumber tidak dikenal"* (Install from unknown sources), masuk ke **Pengaturan (Settings)**, lalu aktifkan opsi **Izinkan dari sumber ini**.
5. Lanjutkan proses instalasi hingga selesai.
6. Buka aplikasi EcoScan AI dan berikan izin akses **Kamera** saat pertama kali diminta. Aplikasi siap digunakan!

Ketentuan & Persyaratan Sistem
* **Sistem Operasi:** Minimum Android 9.0 (Pie) atau yang lebih baru (API Level 28+).
* **Perizinan (Permissions):** Aplikasi ini **wajib** diberikan akses Kamera (`android.permission.CAMERA`) agar fitur pemindai AI dapat berjalan.
* **Koneksi Internet:** Fitur pemindai dan riwayat berjalan secara *offline* (tanpa internet). Namun, fitur "Cari Tempat Sampah Terdekat" membutuhkan koneksi internet dan aplikasi Google Maps yang terinstal di perangkat.
* **Kinerja AI:** Model AI telah dioptimalkan (*fine-tuned MobileNetV2*), pastikan pencahayaan cukup dan kamera fokus pada satu objek sampah untuk hasil persentase yang maksimal.
