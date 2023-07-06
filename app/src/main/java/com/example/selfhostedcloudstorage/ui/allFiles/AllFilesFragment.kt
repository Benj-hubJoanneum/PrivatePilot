package com.example.selfhostedcloudstorage.ui.allFiles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.selfhostedcloudstorage.databinding.FragmentAllFilesBinding
import com.example.selfhostedcloudstorage.mockData.MockService
import com.example.selfhostedcloudstorage.mockData.fileItem.FileItemAdapter
import com.example.selfhostedcloudstorage.mockData.fileItem.FileItemViewModel

class AllFilesFragment : Fragment() {

    private var _binding: FragmentAllFilesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllFilesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val allFilesViewModel = ViewModelProvider(this).get(AllFilesViewModel::class.java)

        // Get data source
        val mockService = MockService()
        val documentsDirectory = mockService.documentsDirectory
        val fileList = documentsDirectory.files.map { FileItemViewModel(it) }.toMutableList()

        // Set up RecyclerView and its adapter
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        val fileAdapter = FileItemAdapter(fileList)
        recyclerView.adapter = fileAdapter

        val textView: TextView = binding.textAllFiles
        allFilesViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
