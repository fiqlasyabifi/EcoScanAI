package com.example.ecoscanai.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ecoscanai.R
import com.example.ecoscanai.databinding.FragmentHomeBinding
import com.example.ecoscanai.ui.history.HistoryViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Siapkan variabel ViewModel untuk membaca database
    private lateinit var historyViewModel: HistoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Sambungkan ViewModel ke Fragment ini
        historyViewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)

        // 2. Pasang "CCTV" (Observer) untuk memantau jumlah data di database secara real-time
        historyViewModel.allHistory.observe(viewLifecycleOwner) { historyList ->
            // Hitung total data yang ada di dalam riwayat
            val totalScanned = historyList.size.toString()

            // Tembakkan angkanya ke layar UI
            binding.tvCountTotal.text = totalScanned
            binding.tvCountSaved.text = totalScanned
        }

        // 3. Hidupkan Tombol Utama "Pindai Sampah"
        binding.btnMainScan.setOnClickListener {
            // Karena MainActivity menggunakan transaksi Fragment manual dan bukan NavComponent,
            // kita memicu navigasi dengan memindah item terpilih pada BottomNavigationView.
            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNav.selectedItemId = R.id.navigation_scan
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}