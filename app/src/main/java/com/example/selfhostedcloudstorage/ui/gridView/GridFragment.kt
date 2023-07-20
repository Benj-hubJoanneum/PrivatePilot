package com.example.selfhostedcloudstorage.ui.gridView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.selfhostedcloudstorage.MainActivity
import com.example.selfhostedcloudstorage.databinding.FragmentListviewBinding

class GridFragment : Fragment() {
    private var _binding: FragmentListviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var gridAdapter: GridAdapter
    private lateinit var gridViewModel: GridViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListviewBinding.inflate(inflater, container, false)
        val root: View = binding.root
        gridViewModel = ViewModelProvider(this)[GridViewModel::class.java]

        // Initialize fileAdapter
        gridAdapter = GridAdapter(emptyList(), requireActivity() as MainActivity)
        gridViewModel.fileList.observe(viewLifecycleOwner) { fileList ->
            gridAdapter.updateList(fileList)
        }

        // Setup RecyclerView
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = gridAdapter

        val textView: TextView = binding.textAllFiles
        gridViewModel.text.observe(viewLifecycleOwner) { text ->
            textView.text = text
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
