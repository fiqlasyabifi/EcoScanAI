package com.example.ecoscanai.ui.history

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ecoscanai.databinding.ItemHistoryBinding
import com.example.ecoscanai.data.local.GarbageEntity

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    // Siapkan list kosong yang siap diisi data dari database
    private var listHistory = emptyList<GarbageEntity>()

    class ViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GarbageEntity) {
            binding.tvHistoryType.text = item.trashType
            binding.tvHistoryConfidence.text = item.confidenceScore
            binding.tvHistoryDate.text = item.date

            if (item.imageUri != null) {
                val uri = android.net.Uri.parse(item.imageUri)
                binding.imgHistoryThumbnail.setImageURI(uri)
            } else {
                binding.imgHistoryThumbnail.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            // 👇 TAMBAHKAN BLOK KLIK INI 👇
            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = android.content.Intent(context, com.example.ecoscanai.ui.result.ResultActivity::class.java)

                // Bawa semua data yang sudah tersimpan
                intent.putExtra("EXTRA_IMAGE_URI", item.imageUri)
                intent.putExtra("EXTRA_TRASH_TYPE", item.trashType)
                intent.putExtra("EXTRA_CONFIDENCE", item.confidenceScore)

                // Ini "Tombol Rahasia"-nya agar AI tidak scan ulang
                intent.putExtra("EXTRA_IS_FROM_HISTORY", true)

                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listHistory[position])
    }

    override fun getItemCount(): Int = listHistory.size

    fun setData(newList: List<GarbageEntity>) {
        listHistory = newList
        notifyDataSetChanged()
    }
}