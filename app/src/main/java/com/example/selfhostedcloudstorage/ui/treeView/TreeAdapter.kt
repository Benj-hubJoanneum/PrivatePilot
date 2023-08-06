package com.example.selfhostedcloudstorage.ui.treeView

import android.graphics.Color
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.selfhostedcloudstorage.MainActivity
import com.example.selfhostedcloudstorage.R
import com.example.selfhostedcloudstorage.databinding.DirectoryNodeBinding
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryItemViewModel

class TreeAdapter(
    private var itemList: List<DirectoryItemViewModel>,
    private val mainActivity: MainActivity
) : RecyclerView.Adapter<TreeAdapter.DirViewHolder>(), ActionMode.Callback {

    private val selectedItems = mutableListOf<Int>()
    private var actionMode: ActionMode? = null

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
        val paddingLeftDp = currentItem.depth * 25 * density.toInt()
        holder.itemView.setPadding(paddingLeftDp, 0, 0, 0)
    }

    fun updateList(newItemList: List<DirectoryItemViewModel>) {
        itemList = newItemList
        notifyDataSetChanged()
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.action_mode_menu, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_delete -> {
                deleteSelectedItems()
                mode?.finish()
                return true
            }
        }
        return false
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

    override fun onDestroyActionMode(mode: ActionMode?) {
        actionMode = null
        mainActivity.invalidateOptionsMenu()
    }

    inner class DirViewHolder(private val binding: DirectoryNodeBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {

        init {
            binding.root.setOnClickListener(this)
            binding.root.setOnLongClickListener(this)
        }

        private fun toggleSelection(position: Int) {
            if (selectedItems.contains(position)) {
                selectedItems.remove(position)
                if (selectedItems.size < 1) {
                    actionMode?.finish()
                }
            } else {
                selectedItems.add(position)
                if (actionMode == null) {
                    actionMode = mainActivity.startActionMode(this@TreeAdapter)
                }
            }
            notifyItemChanged(position)
        }

        override fun onClick(v: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                if (selectedItems.size < 1) {
                    val directoryItem = itemList[position]
                    val message = "${directoryItem.name} clicked"
                    Toast.makeText(itemView.context, message, Toast.LENGTH_SHORT).show()
                } else {
                    toggleSelection(position)
                }
            }
        }

        override fun onLongClick(v: View?): Boolean {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                toggleSelection(position)
                return true
            }
            return false
        }

        fun bind(directoryItem: DirectoryItemViewModel) {
            binding.viewModel = directoryItem
            binding.executePendingBindings()
        }
    }
}
