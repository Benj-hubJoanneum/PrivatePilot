package com.example.selfhostedcloudstorage.ui.treeView

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.selfhostedcloudstorage.R
import com.example.selfhostedcloudstorage.databinding.DirectoryNodeBinding
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryItemViewModel

class NavAdapter(
    private var itemList: List<DirectoryItemViewModel>
) : RecyclerView.Adapter<NavAdapter.DirViewHolder>() {

    private val selectedItems = mutableListOf<Int>()

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
        val isSelected = selectedItems.contains(position)
        holder.itemView.isSelected = isSelected
        holder.itemView.setBackgroundColor(
            if (isSelected) {
                ContextCompat.getColor(holder.itemView.context, R.color.selectedItemBackground)
            } else {
                Color.TRANSPARENT
            }
        )

        //setting padding
        val density = holder.itemView.context.resources.displayMetrics.density
        val paddingLeftDp = currentItem.depth * 30 * density.toInt()
        holder.itemView.setPadding(paddingLeftDp, 0, 0, 0)
    }

    fun updateList(newItemList: List<DirectoryItemViewModel>) {
        itemList = newItemList
        notifyDataSetChanged()
    }

    private fun deleteSelectedItems() {
        val selectedItemsList = selectedItems.toList()
        selectedItemsList.sortedDescending().forEach { position ->
            itemList = itemList.filterIndexed { index, _ -> index != position }
            notifyItemRemoved(position)
        }
        selectedItems.clear()
        notifyDataSetChanged()
    }


    inner class DirViewHolder(private val binding: DirectoryNodeBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        private fun toggleSelection(position: Int) {
            if (selectedItems.contains(position)) {
                selectedItems.remove(position)
            } else {
                selectedItems.add(position)

            }
            notifyItemChanged(position)
        }

        override fun onClick(v: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                if (selectedItems.size < 1) {
                    val directoryItem = itemList[position]
                    val message = "${directoryItem.path} clicked"
                    Toast.makeText(itemView.context, message, Toast.LENGTH_SHORT).show()
                } else {
                    toggleSelection(position)
                }
            }
        }

        fun bind(directoryItem: DirectoryItemViewModel) {
            binding.viewModel = directoryItem
            binding.executePendingBindings()
        }
    }
}
