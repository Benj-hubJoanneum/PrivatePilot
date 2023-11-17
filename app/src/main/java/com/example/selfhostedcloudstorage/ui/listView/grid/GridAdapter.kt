package com.example.selfhostedcloudstorage.ui.listView.grid

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.selfhostedcloudstorage.MainActivity
import com.example.selfhostedcloudstorage.databinding.FileItemBinding
import com.example.selfhostedcloudstorage.model.nodeItem.viewmodel.NodeItemViewModel
import com.example.selfhostedcloudstorage.ui.listView.base.BaseAdapter

class GridAdapter(
    itemList: List<NodeItemViewModel>,
    mainActivity: MainActivity
) : BaseAdapter(itemList, mainActivity) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FileItemBinding.inflate(inflater, parent, false)
        return FileViewHolder(binding)
    }

    inner class FileViewHolder(private val binding: FileItemBinding) :
        BaseViewHolder(binding) {

        override fun bind(fileItem: NodeItemViewModel) {
            fileItem.image = getItemImage(fileItem)
            binding.viewModel = fileItem
            binding.executePendingBindings()
        }
    }
}
