package com.example.selfhostedcloudstorage.ui.listView.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.selfhostedcloudstorage.MainActivity
import com.example.selfhostedcloudstorage.databinding.FileNodeBinding
import com.example.selfhostedcloudstorage.model.nodeItem.viewmodel.NodeItemViewModel
import com.example.selfhostedcloudstorage.ui.listView.base.BaseAdapter

class ListAdapter(
    itemList: List<NodeItemViewModel>,
    mainActivity: MainActivity
) : BaseAdapter(itemList, mainActivity) {

    init {
        if (nodeRepository.selectedItems.size > 0 || nodeRepository.cutItems.size > 0) {
            actionMode = mainActivity.startActionMode(this@ListAdapter)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FileNodeBinding.inflate(inflater, parent, false)
        return FileViewHolder(binding)
    }

    inner class FileViewHolder(private val binding: FileNodeBinding) :
        BaseViewHolder(binding) {

        override fun bind(fileItem: NodeItemViewModel) {
            fileItem.icon = getItemImage(fileItem)
            binding.viewModel = fileItem
            binding.executePendingBindings()
        }
    }
}
