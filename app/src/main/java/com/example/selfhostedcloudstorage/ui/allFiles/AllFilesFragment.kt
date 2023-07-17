package com.example.selfhostedcloudstorage.ui.allFiles

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.selfhostedcloudstorage.MainActivity
import com.example.selfhostedcloudstorage.databinding.FragmentAllFilesBinding
import com.example.selfhostedcloudstorage.mockData.fileItem.FileItemAdapter

class AllFilesFragment : Fragment() {
    private var _binding: FragmentAllFilesBinding? = null
    private val binding get() = _binding!!

    private lateinit var fileAdapter: FileItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllFilesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val allFilesViewModel = ViewModelProvider(this).get(AllFilesViewModel::class.java)

        // Initialize fileAdapter
        fileAdapter = FileItemAdapter(emptyList(), requireActivity() as MainActivity)
        allFilesViewModel.fileList.observe(viewLifecycleOwner) { fileList ->
            fileAdapter.updateList(fileList)
        }

        // Setup RecyclerView
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = fileAdapter

        val textView: TextView = binding.textAllFiles
        allFilesViewModel.text.observe(viewLifecycleOwner) { text ->
            textView.text = text
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
