package com.example.selfhostedcloudstorage.mockData.fileItem

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Handler
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.selfhostedcloudstorage.R
import com.example.selfhostedcloudstorage.mockData.model.FileType

class FileItemAdapter(
    private val fileList: List<FileItemViewModel>
) : RecyclerView.Adapter<FileItemAdapter.FileViewHolder>() {

    private val selectedItems = mutableListOf<Int>()
    private val longPressDuration = 2000L // 2 seconds
    private var longPressHandler: Handler? = null
    private var isLongPressed = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.file_item, parent, false)
        return FileViewHolder(itemView)
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

    override fun getItemCount(): Int {
        return fileList.size
    }

    private fun getItemImage(context: Context, type: FileType): Drawable {
        return when (type) {
            FileType.JPG -> context.getDrawable(R.drawable.ic_image_24)!!
            FileType.PDF -> context.getDrawable(R.drawable.ic_pdf_24)!!
            else -> context.getDrawable(R.drawable.ic_document)!!
        }
    }

    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnTouchListener {
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        private val icon: ImageView = itemView.findViewById(R.id.imageView)
        private var longPressRunnable: Runnable? = null
        private var longPressHandler: Handler? = null

        init {
            itemView.setOnTouchListener(this)
            itemView.setOnClickListener { triggerItem(adapterPosition) }
        }

        override fun onTouch(view: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startLongPressHandler()
                    itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.selectedItemBackground))
                }
                MotionEvent.ACTION_UP -> {
                    cancelLongPressHandler()
                    itemView.setBackgroundColor(Color.TRANSPARENT)
                    if (!isLongPressed) {
                        triggerItem(adapterPosition)
                    }
                    isLongPressed = false
                }
                MotionEvent.ACTION_CANCEL -> {
                    cancelLongPressHandler()
                    itemView.setBackgroundColor(Color.TRANSPARENT)
                    isLongPressed = false
                }
            }
            return true
        }

        private fun triggerItem(position: Int) {
            //execute on click
            if (selectedItems.size < 1) {
                val fileItem = fileList[position]
                val message = "${fileItem.name} clicked"
                Toast.makeText(itemView.context, message, Toast.LENGTH_SHORT).show()
            } else { //select multiple if it is activated
                toggleSelection(position)
            }
        }

        private fun startLongPressHandler() {
            longPressHandler = Handler()
            longPressRunnable = Runnable {
                isLongPressed = true
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
        }


        fun bind(fileItem: FileItemViewModel) {
            descriptionTextView.text = fileItem.name
            icon.setImageDrawable(getItemImage(itemView.context, fileItem.type as FileType))
        }
    }
}
