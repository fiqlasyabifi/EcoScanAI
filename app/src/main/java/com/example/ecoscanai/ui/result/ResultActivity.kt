package com.example.ecoscanai.ui.result

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.ecoscanai.databinding.ActivityResultBinding
import com.example.ecoscanai.ml.GarbageClassifier
import com.example.ecoscanai.data.local.GarbageEntity
import com.example.ecoscanai.ui.history.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var classifier: GarbageClassifier

    // Variabel untuk menyambungkan ke Database
    private lateinit var historyViewModel: HistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi ViewModel Database
        try {
            historyViewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        } catch (e: Exception) {
            Log.e("ResultActivity", "Gagal memuat ViewModel History", e)
        }

        // Reset teks default agar tidak membingungkan jika AI gagal
        binding.tvWasteClassification.text = "Menganalisis..."
        binding.tvAccuracyPercent.text = "- %"

        try {
            classifier = GarbageClassifier(this)
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal memuat model AI: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("ResultActivity", "Model Load Error", e)
        }

        val imageUriString = intent.getStringExtra("EXTRA_IMAGE_URI")
        // Tangkap tombol rahasianya (default bernilai false jika dari kamera)
        val isFromHistory = intent.getBooleanExtra("EXTRA_IS_FROM_HISTORY", false)

        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            binding.imgCapturedResult.setImageURI(imageUri)

            if (isFromHistory) {
                // JIKA DARI RIWAYAT: Langsung tampilkan teks, JANGAN jalankan AI!
                val savedType = intent.getStringExtra("EXTRA_TRASH_TYPE") ?: "Tidak diketahui"
                val savedScore = intent.getStringExtra("EXTRA_CONFIDENCE") ?: "- %"

                // Ubah judul paling atas agar user tahu ini riwayat
                binding.tvTitlePage.text = "Detail Riwayat"

                binding.tvWasteClassification.text = savedType
                binding.tvAccuracyPercent.text = savedScore
                binding.tvWasteSubtitle.text = getTipsDescription(savedType)
                updateDaurUlangTips(savedType)

            } else {
                // JIKA DARI KAMERA: Jalankan AI seperti biasa
                try {
                    val bitmap = uriToBitmap(imageUri)
                    runImageClassification(bitmap) // Ini juga akan memanggil saveToHistory
                } catch (e: Exception) {
                    Toast.makeText(this, "Kesalahan AI: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    Log.e("ResultActivity", "Classification Error", e)
                    binding.tvWasteClassification.text = "Gagal Analisis"
                }
            }

        } else {
            Toast.makeText(this, "Gagal memuat gambar sampah", Toast.LENGTH_SHORT).show()
        }

        binding.btnFindNearestBin.setOnClickListener {
            // Kita bisa membuat pencariannya lebih pintar berdasarkan jenis sampah
            val trashType = binding.tvWasteClassification.text.toString()
            val keyword = if (trashType == "Plastik" || trashType == "Kertas" || trashType == "Kardus" || trashType == "Logam / Aluminium") {
                "Bank Sampah terdekat" // Karena barang ini punya nilai jual
            } else {
                "Tempat Pembuangan Sampah terdekat"
            }

            // Membuat perintah pencarian untuk Google Maps
            val gmmIntentUri = android.net.Uri.parse("geo:0,0?q=$keyword")
            val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps") // Spesifik buka di Google Maps

            try {
                startActivity(mapIntent)
            } catch (e: android.content.ActivityNotFoundException) {
                // Jika pengguna tidak punya aplikasi Google Maps, buka lewat browser
                val browserUri = android.net.Uri.parse("https://www.google.com/maps/search/$keyword")
                val browserIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, browserUri)
                startActivity(browserIntent)
            }
        }
    }

    private fun uriToBitmap(uri: Uri): Bitmap {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                decoder.isMutableRequired = true
            }
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        }
        return bitmap.copy(Bitmap.Config.ARGB_8888, true)
    }

    private fun runImageClassification(bitmap: Bitmap) {
        val result = classifier.classify(bitmap)

        val trashType = result.first
        val confidence = result.second

        // Kita pisahkan angka persentasenya saja untuk disimpan ke DB
        val cleanConfidence = String.format("%.0f%%", confidence)

        binding.tvWasteClassification.text = trashType
        binding.tvAccuracyPercent.text = "$cleanConfidence TINGKAT KEYAKINAN"
        binding.tvWasteSubtitle.text = getTipsDescription(trashType)

        updateDaurUlangTips(trashType)

        // Simpan Hasil ke Database Riwayat
        val imageUriString = intent.getStringExtra("EXTRA_IMAGE_URI")
        saveToHistory(trashType, cleanConfidence, imageUriString)
    }

    private fun saveToHistory(type: String, score: String, uri: String?) {
        val currentDate = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
        val historyEntry = GarbageEntity(
            trashType = type,
            confidenceScore = score,
            date = currentDate,
            imageUri = uri
        )

        try {
            historyViewModel.insert(historyEntry)
            Log.d("ResultActivity", "Data berhasil disimpan ke riwayat!")
        } catch (e: Exception) {
            Log.e("ResultActivity", "Gagal menyimpan ke database", e)
        }
    }

    private fun getTipsDescription(type: String): String {
        return when (type) {
            "Kardus" -> "Lipat pipih kardus sebelum dibuang untuk menghemat ruang penyimpanan."
            "Kaca" -> "Hati-hati saat membuang. Pisahkan pecahan kaca dan bungkus dengan aman."
            "Logam / Aluminium" -> "Bilas bersih kaleng atau logam untuk mencegah bau dan karat."
            "Kertas" -> "Pastikan kertas kering dan bersih dari noda minyak atau sisa makanan."
            "Plastik" -> "Dapat didaur ulang. Bilas hingga bersih dan pisahkan dari sisa makanan."
            else -> "Pisahkan dan buang sesuai dengan jenis limbah pada tempat sampah yang tepat."
        }
    }

    private fun updateDaurUlangTips(type: String) {
        when (type) {
            "Kertas" -> {
                binding.tvTip1.text = "Pisahkan dari sampah basah agar kertas tidak hancur dan berjamur."
                binding.tvTip2.text = "Dokumen yang mengandung informasi pribadi sebaiknya dirobek terlebih dahulu."
                binding.tvTip3.text = "Kertas yang berlapis plastik/minyak tidak bisa didaur ulang dengan kertas biasa."
            }
            "Plastik" -> {
                binding.tvTip1.text = "Bilas hingga bersih sebelum dibuang untuk mencegah kontaminasi."
                binding.tvTip2.text = "Remas botol plastik untuk menghemat ruang di tempat sampah."
                binding.tvTip3.text = "Lepas tutup botol jika diwajibkan oleh fasilitas setempat."
            }
            "Kardus" -> {
                binding.tvTip1.text = "Lipat pipih kardus sebelum dibuang untuk menghemat ruang penyimpanan."
                binding.tvTip2.text = "Pastikan kardus dalam keadaan kering, kardus basah sulit didaur ulang."
                binding.tvTip3.text = "Lepaskan lakban atau pita perekat yang masih menempel pada kardus."
            }
            "Logam / Aluminium" -> {
                binding.tvTip1.text = "Bilas sisa makanan/minuman pada kaleng sebelum dibuang."
                binding.tvTip2.text = "Hati-hati dengan tepi kaleng yang tajam saat memipihkannya."
                binding.tvTip3.text = "Kumpulkan kaleng dalam keadaan kering untuk mencegah karat."
            }
            "Kaca" -> {
                binding.tvTip1.text = "Bungkus pecahan kaca dengan kertas tebal atau koran agar aman."
                binding.tvTip2.text = "Pisahkan kaca bening dan kaca berwarna jika memungkinkan."
                binding.tvTip3.text = "Bilas botol atau toples kaca dari sisa isi di dalamnya."
            }
            else -> {
                binding.tvTip1.text = "Buang sampah pada tempat yang sesuai dengan jenisnya."
                binding.tvTip2.text = "Pisahkan sampah organik dan anorganik di rumah."
                binding.tvTip3.text = "Jaga kebersihan lingkungan sekitar."
            }
        }
    }
}