package com.example.selfhostedcloudstorage.ui.listView.base.horizontalList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.selfhostedcloudstorage.R
import com.example.selfhostedcloudstorage.databinding.HorizontalListItemBinding
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryBreadcrumbViewModel
import com.example.selfhostedcloudstorage.restapi.service.NodeRepository

class HorizontalListAdapter : RecyclerView.Adapter<HorizontalListAdapter.HorizontalViewHolder>() {

    private var itemList: List<DirectoryBreadcrumbViewModel> = listOf()
    private var nodeRepository = NodeRepository.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HorizontalListItemBinding.inflate(inflater, parent, false)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.horizontal_list_item, parent, false)
        return HorizontalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HorizontalViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            nodeRepository.readNode(item.path)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun updateList(newItemList: List<DirectoryBreadcrumbViewModel>) {
        itemList = newItemList
        notifyDataSetChanged()
    }

    inner class HorizontalViewHolder(private val binding: HorizontalListItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }
        override fun onClick(v: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val clickedFolder = itemList[position]
                nodeRepository.readNode(clickedFolder.path)
            }
        }

        fun bind(directoryItem: DirectoryBreadcrumbViewModel) {
            binding.viewModel = directoryItem
            binding.executePendingBindings()
        }
    }
}
