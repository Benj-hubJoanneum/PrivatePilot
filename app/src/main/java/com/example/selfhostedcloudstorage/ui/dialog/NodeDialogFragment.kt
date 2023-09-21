package com.example.selfhostedcloudstorage.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.selfhostedcloudstorage.R
import com.example.selfhostedcloudstorage.databinding.NodeDialogBinding
import com.example.selfhostedcloudstorage.model.nodeItem.NodeItemViewModel
import com.example.selfhostedcloudstorage.restapi.service.NodeRepository

class NodeDialogFragment(
    private val context: Context,
    private val node: NodeItemViewModel
) : DialogFragment() {
    private var _binding: NodeDialogBinding? = null
    private val binding get() = _binding!!

    private val nodeRepository = NodeRepository.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NodeDialogBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.viewModel = node

        binding.closeButton.setOnClickListener {
            dismiss()
        }
        binding.downloadButton.setOnClickListener {
            nodeRepository.downloadFile(node.path, context)
        }
        return root
    }

    override fun onResume() {
        super.onResume()

        // Calculate the desired width (screen width - 20sp)
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val desiredWidth = screenWidth - resources.getDimensionPixelSize(R.dimen.dialog_margin)

        // Set the dialog's width
        dialog?.window?.setLayout(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
