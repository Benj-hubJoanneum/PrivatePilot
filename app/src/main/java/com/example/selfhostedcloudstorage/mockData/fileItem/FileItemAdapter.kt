package com.example.selfhostedcloudstorage.mockData.fileItem

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.selfhostedcloudstorage.R
import com.example.selfhostedcloudstorage.databinding.FileItemBinding
import com.example.selfhostedcloudstorage.mockData.model.FileType

class FileItemAdapter(
    private var fileList: List<FileItemViewModel>,
    private val itemSelectionListener: ItemSelectionListener
) : RecyclerView.Adapter<FileItemAdapter.FileViewHolder>() {

    private val selectedItems = mutableListOf<Int>()
    private var longPressHandler: Handler? = null
    private val longPressDuration = 500L // 0.5 second

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

    inner class FileViewHolder(private val binding: FileItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnTouchListener {

        private var longPressRunnable: Runnable? = null

        init {
            binding.root.setOnClickListener(this)
            binding.root.setOnTouchListener(this)
        }


        override fun onTouch(view: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startLongPressHandler()
                    itemView.setBackgroundColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.selectedItemBackground
                        )
                    )
                }
                MotionEvent.ACTION_UP -> {
                    cancelLongPressHandler()
                    itemView.setBackgroundColor(Color.TRANSPARENT)
                    onClick(view)
                }
                MotionEvent.ACTION_CANCEL -> {
                    cancelLongPressHandler()
                    itemView.setBackgroundColor(Color.TRANSPARENT)
                }
            }
            return true
        }

        private fun startLongPressHandler() {
            cancelLongPressHandler()
            longPressHandler = Handler()
            longPressRunnable = Runnable {
                toggleSelection(adapterPosition)
            }
            longPressHandler?.postDelayed(longPressRunnable!!, longPressDuration)
        }

        private fun cancelLongPressHandler() {
            longPressRunnable?.let { longPressHandler?.removeCallbacks(it) }
        }

        private fun toggleSelection(position: Int) {
            if (selectedItems.contains(position)) {
                selectedItems.remove(position)
            } else {
                selectedItems.add(position)
            }
            notifyItemChanged(position)
            itemSelectionListener.onItemsSelected(selectedItems.size)
        }

        override fun onClick(v: View) {
            if (selectedItems.size < 1) {
                val fileItem = fileList[adapterPosition]
                val message = "${fileItem.name} clicked"
                Toast.makeText(itemView.context, message, Toast.LENGTH_SHORT).show()
            } else {
                toggleSelection(adapterPosition)
            }
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
