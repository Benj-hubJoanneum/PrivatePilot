package com.example.selfhostedcloudstorage.ui.navView

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.selfhostedcloudstorage.R
import com.example.selfhostedcloudstorage.databinding.DirectoryNodeBinding
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryItemViewModel
import com.example.selfhostedcloudstorage.restapi.service.ApiService

class NavAdapter(
    private var itemList: List<DirectoryItemViewModel>,
    private val navModel: NavModel

) : RecyclerView.Adapter<NavAdapter.DirViewHolder>() {

    private var selectedFolder: DirectoryItemViewModel? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DirectoryNodeBinding.inflate(inflater, parent, false)
        return DirViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DirViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.bind(currentItem)

        // Set the item's selection state
        val isSelected = currentItem.path == selectedFolder?.path
        holder.itemView.isSelected = isSelected
        holder.itemView.setBackgroundColor(
            if (isSelected) {
                ContextCompat.getColor(holder.itemView.context, R.color.selectedItemBackground)
            } else {
                Color.TRANSPARENT
            }
        )

        // Setting padding
        val density = holder.itemView.context.resources.displayMetrics.density
        val paddingLeftDp = currentItem.depth * 30 * density.toInt()
        holder.itemView.setPadding(paddingLeftDp, 0, 0, 0)
    }

    fun updateList(newItemList: List<DirectoryItemViewModel>) {
        itemList = newItemList
        notifyDataSetChanged()
    }

    private fun selectedFolderChanged(selectedFolder: DirectoryItemViewModel) {
        this.selectedFolder = selectedFolder
        navModel.setSelectedFolder(selectedFolder)
        notifyDataSetChanged()
    }

    inner class DirViewHolder(private val binding: DirectoryNodeBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }
        override fun onClick(v: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val clickedFolder = itemList[position]
                selectedFolderChanged(clickedFolder)
            }
        }

        fun bind(directoryItem: DirectoryItemViewModel) {
            binding.viewModel = directoryItem
            binding.executePendingBindings()
        }
    }
}
