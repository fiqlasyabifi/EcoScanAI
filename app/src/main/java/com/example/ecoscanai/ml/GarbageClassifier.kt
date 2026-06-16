// Perbarui file app/src/main/java/com/example/ecoscanai/ml/GarbageClassifier.kt
package com.example.ecoscanai.ml

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import java.nio.MappedByteBuffer
import kotlin.math.min

class GarbageClassifier(context: Context) {

    private var interpreter: Interpreter

    /**
     * PERBAIKAN URUTAN LABEL:
     * Berdasarkan tes Anda, botol plastik terdeteksi di Index 0.
     * Maka kita asumsikan Index 0 adalah "plastic".
     */
    private val labels = listOf("cardboard", "glass", "metal", "paper", "plastic", "trash")

    init {
        val modelBuffer: MappedByteBuffer = FileUtil.loadMappedFile(context, "mobilenetv2_garbage_finetuned.tflite")
        val options = Interpreter.Options().apply {
            setNumThreads(4)
        }
        interpreter = Interpreter(modelBuffer, options)
    }

    fun classify(bitmap: Bitmap): Pair<String, Float> {
        val size = min(bitmap.width, bitmap.height)

        // Menggunakan normalisasi 0 ke 1 (NormalizeOp(0f, 255f))
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeWithCropOrPadOp(size, size))
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0.0f, 255.0f))
            .build()

        var tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(bitmap)
        tensorImage = imageProcessor.process(tensorImage)

        val outputBuffer = Array(1) { FloatArray(labels.size) }
        interpreter.run(tensorImage.buffer, outputBuffer)

        val results = outputBuffer[0]

        Log.e("AI_DEBUG", "========== HASIL ANALISIS BARU ==========")
        val sortedIndices = results.indices.sortedByDescending { results[it] }
        for (i in 0 until min(3, sortedIndices.size)) {
            val idx = sortedIndices[i]
            Log.e("AI_DEBUG", "Top ${i + 1}: ${labels[idx]} (${String.format("%.2f", results[idx] * 100)}%)")
        }

        val maxIdx = sortedIndices[0]
        val maxVal = results[maxIdx]

        val labelIndonesian = when (labels[maxIdx]) {
            "cardboard" -> "Kardus"
            "glass" -> "Kaca"
            "metal" -> "Logam / Aluminium"
            "paper" -> "Kertas"
            "plastic" -> "Plastik"
            "trash" -> "Sampah Lainnya"
            else -> "Tidak Terdeteksi"
        }

        return Pair(labelIndonesian, maxVal * 100)
    }
}
