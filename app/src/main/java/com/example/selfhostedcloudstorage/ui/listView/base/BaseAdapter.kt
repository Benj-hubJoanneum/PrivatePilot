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
import com.example.selfhostedcloudstorage.restapi.service.NodeRepository
import com.example.selfhostedcloudstorage.ui.dialog.NodeDialogFragment

abstract class BaseAdapter(
    protected var itemList: List<NodeItemViewModel>,
    protected val mainActivity: MainActivity
) : RecyclerView.Adapter<BaseAdapter.BaseViewHolder>(), ActionMode.Callback  {

    protected val selectedItems = mutableListOf<Int>()
    protected var actionMode: ActionMode? = null
    private val nodeRepository = NodeRepository.getInstance()

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
        selectedItems.forEach{position ->
            nodeRepository.deleteNode(itemList[position].path)
        }
        selectedItems.clear()
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
            if (adapterPosition == RecyclerView.NO_POSITION) return

            val fileItem = itemList[adapterPosition]

            if (selectedItems.size < 1) {
                if (fileItem.type == FileType.FOLDER)
                    nodeRepository.readNode(fileItem.path)
                else {
                    nodeRepository.pointer = fileItem.path
                    NodeDialogFragment(
                        mainActivity,
                        fileItem
                    ).show(mainActivity.supportFragmentManager, "NodeDialog")
                }

            } else toggleSelection(adapterPosition)
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
                FileType.PDF -> AppCompatResources.getDrawable(itemView.context, R.drawable.ic_pdf)
                FileType.TXT -> AppCompatResources.getDrawable(itemView.context, R.drawable.ic_text)
                FileType.XLSX -> AppCompatResources.getDrawable(itemView.context, R.drawable.ic_table)
                FileType.JPG -> AppCompatResources.getDrawable(itemView.context, R.drawable.ic_image)
                FileType.JPEG -> AppCompatResources.getDrawable(itemView.context, R.drawable.ic_image)
                FileType.PNG -> AppCompatResources.getDrawable(itemView.context, R.drawable.ic_image)
                FileType.DOC -> AppCompatResources.getDrawable(itemView.context, R.drawable.ic_document)
                FileType.FOLDER -> AppCompatResources.getDrawable(itemView.context, R.drawable.ic_folder)
                else -> AppCompatResources.getDrawable(itemView.context, R.drawable.ic_document)
            }
        }

        abstract fun bind(fileItem: NodeItemViewModel)
    }
}
