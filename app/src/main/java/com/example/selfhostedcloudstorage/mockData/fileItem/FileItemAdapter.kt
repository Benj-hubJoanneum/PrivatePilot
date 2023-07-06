package com.example.selfhostedcloudstorage.mockData.fileItem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.selfhostedcloudstorage.R

class FileItemAdapter(
    private val fileList: List<FileItemViewModel>
) : RecyclerView.Adapter<FileItemAdapter.FileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.file_item, parent, false)
        return FileViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val currentItem = fileList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val fileItem = fileList[position]
                    val message = "${fileItem.name} clicked"
                    Toast.makeText(itemView.context, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        fun bind(fileItem: FileItemViewModel) {
            descriptionTextView.text = fileItem.name
        }
    }
}