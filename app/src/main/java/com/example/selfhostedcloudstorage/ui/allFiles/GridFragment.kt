package com.example.selfhostedcloudstorage.ui.allFiles

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

class GridFragment : Fragment() {
    private var _binding: FragmentGridviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var gridAdapter: GridAdapter
    private lateinit var gridViewmodel: GridViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGridviewBinding.inflate(inflater, container, false)
        val root: View = binding.root
        gridViewmodel = ViewModelProvider(this).get(GridViewModel::class.java)

        // Initialize fileAdapter
        gridAdapter = GridAdapter(emptyList(), requireActivity() as MainActivity)
        gridViewmodel.fileList.observe(viewLifecycleOwner) { fileList ->
            gridAdapter.updateList(fileList)
        }

        // Setup RecyclerView
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = gridAdapter

        val textView: TextView = binding.textAllFiles
        gridViewmodel.text.observe(viewLifecycleOwner) { text ->
            textView.text = text
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
