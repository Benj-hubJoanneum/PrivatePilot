package com.example.selfhostedcloudstorage.ui.treeView

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

class TreeFragment : Fragment() {
    private var _binding: FragmentListviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var treeAdapter: TreeAdapter
    private lateinit var treeModel: TreeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListviewBinding.inflate(inflater, container, false)
        val root: View = binding.root
        treeModel = ViewModelProvider(this)[TreeViewModel::class.java]

        // Initialize fileAdapter
        treeAdapter = TreeAdapter(emptyList(), requireActivity() as MainActivity)
        treeModel.itemList.observe(viewLifecycleOwner) { directoryList ->
            treeAdapter.updateList(directoryList)
        }

        // Setup RecyclerView
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = treeAdapter

        val textView: TextView = binding.textAllFiles
        treeModel.text.observe(viewLifecycleOwner) { text ->
            textView.text = text
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}