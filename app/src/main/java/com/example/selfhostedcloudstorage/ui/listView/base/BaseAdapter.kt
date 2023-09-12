package com.example.selfhostedcloudstorage.ui.listView.base

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.selfhostedcloudstorage.MainActivity
import com.example.selfhostedcloudstorage.R
import com.example.selfhostedcloudstorage.model.FileType
import com.example.selfhostedcloudstorage.model.nodeItem.NodeItemViewModel
import com.example.selfhostedcloudstorage.ui.dialog.NodeDialogFragment

abstract class BaseAdapter(
    protected var itemList: List<NodeItemViewModel>,
    protected val mainActivity: MainActivity
) : RecyclerView.Adapter<BaseAdapter.BaseViewHolder>(), ActionMode.Callback {

    protected val selectedItems = mutableListOf<Int>()
    protected var actionMode: ActionMode? = null

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
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
    }

    fun updateList(newItemList: List<NodeItemViewModel>) {
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

    private fun deleteSelectedItems() { // updated to API ACCESS
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

    abstract inner class BaseViewHolder(private val binding: ViewBinding) :
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
                    actionMode = mainActivity.startActionMode(this@BaseAdapter)
                }
            }
            notifyItemChanged(position)
        }

        override fun onClick(v: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val fileItem = itemList[position]

                if (selectedItems.size < 1) {
                    // Show the NodeDialogFragment when an item is clicked
                    val dialogFragment = NodeDialogFragment(fileItem)
                    val fragmentManager = mainActivity.supportFragmentManager
                    dialogFragment.show(fragmentManager, "NodeDialog")
                } else toggleSelection(position)
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
        protected fun getItemImage(fileItem: NodeItemViewModel): Drawable? {
            return when (fileItem.type) {
                FileType.JPG -> AppCompatResources.getDrawable(itemView.context, R.drawable.ic_image)
                FileType.PDF -> AppCompatResources.getDrawable(itemView.context, R.drawable.ic_pdf)
                else -> AppCompatResources.getDrawable(itemView.context, R.drawable.ic_document)
            }
        }

        abstract fun bind(fileItem: NodeItemViewModel)
    }
}
