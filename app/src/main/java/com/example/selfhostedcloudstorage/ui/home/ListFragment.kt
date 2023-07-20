package com.example.selfhostedcloudstorage.ui.home

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
import com.example.selfhostedcloudstorage.databinding.FragmentGridviewBinding
import com.example.selfhostedcloudstorage.ui.allFiles.GridAdapter
import com.example.selfhostedcloudstorage.ui.allFiles.GridViewModel

class ListFragment : Fragment() {
    private var _binding: FragmentGridviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var fileAdapter: GridAdapter
    private lateinit var gridViewModel: GridViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGridviewBinding.inflate(inflater, container, false)
        val root: View = binding.root
        gridViewModel = ViewModelProvider(this).get(GridViewModel::class.java)

        // Initialize fileAdapter
        fileAdapter = GridAdapter(emptyList(), requireActivity() as MainActivity)
        gridViewModel.fileList.observe(viewLifecycleOwner) { fileList ->
            fileAdapter.updateList(fileList)
        }

        // Setup RecyclerView
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = fileAdapter

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