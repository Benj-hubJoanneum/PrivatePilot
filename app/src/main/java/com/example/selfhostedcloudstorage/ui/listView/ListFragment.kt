package com.example.selfhostedcloudstorage.ui.listView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.selfhostedcloudstorage.MainActivity
import com.example.selfhostedcloudstorage.databinding.FragmentListviewBinding

class ListFragment : Fragment() {
    private var _binding: FragmentListviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var fileAdapter: ListAdapter
    private lateinit var listViewModel: ListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListviewBinding.inflate(inflater, container, false)
        val root: View = binding.root
        listViewModel = ViewModelProvider(this)[ListViewModel::class.java]

        // Initialize fileAdapter
        fileAdapter = ListAdapter(emptyList(), requireActivity() as MainActivity)
        listViewModel.fileList.observe(viewLifecycleOwner) { fileList ->
            fileAdapter.updateList(fileList)
        }

        // Setup RecyclerView
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = fileAdapter

        val textView: TextView = binding.textAllFiles
        listViewModel.text.observe(viewLifecycleOwner) { text ->
            textView.text = text
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
