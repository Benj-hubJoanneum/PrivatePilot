package com.example.selfhostedcloudstorage.ui.allFiles

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.selfhostedcloudstorage.MainActivity
import com.example.selfhostedcloudstorage.R
import com.example.selfhostedcloudstorage.databinding.FileItemBinding
import com.example.selfhostedcloudstorage.mockData.fileItem.FileItemViewModel
import com.example.selfhostedcloudstorage.mockData.model.FileType

class GridAdapter(
    private var fileList: List<FileItemViewModel>,
    private val mainActivity: MainActivity
) : RecyclerView.Adapter<GridAdapter.FileViewHolder>(), ActionMode.Callback {

    private val selectedItems = mutableListOf<Int>()
    private var actionMode: ActionMode? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FileItemBinding.inflate(inflater, parent, false)
        return FileViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val currentItem = fileList[position]

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
    }

    fun updateList(newFileList: List<FileItemViewModel>) {
        fileList = newFileList
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
            fileList = fileList.filterIndexed { index, _ -> index != position }
            notifyItemRemoved(position)
        }
        selectedItems.clear()
        notifyDataSetChanged()
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        actionMode = null
        mainActivity.invalidateOptionsMenu()
    }

    inner class FileViewHolder(private val binding: FileItemBinding) :
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
                    actionMode = mainActivity.startActionMode(this@GridAdapter)
                }
            }
            notifyItemChanged(position)
        }

        override fun onClick(v: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                if (selectedItems.size < 1) {
                    val fileItem = fileList[position]
                    val message = "${fileItem.name} clicked"
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

        fun bind(fileItem: FileItemViewModel) {
            fileItem.image = getItemImage(fileItem)
            binding.viewModel = fileItem
            binding.executePendingBindings()
        }

        private fun getItemImage(fileItem: FileItemViewModel): Drawable? {
            return when (fileItem.type) {
                FileType.JPG -> AppCompatResources.getDrawable(itemView.context, R.drawable.ic_image)
                FileType.PDF -> AppCompatResources.getDrawable(itemView.context, R.drawable.ic_pdf)
                else -> AppCompatResources.getDrawable(itemView.context, R.drawable.ic_document)
            }
        }
    }
}
