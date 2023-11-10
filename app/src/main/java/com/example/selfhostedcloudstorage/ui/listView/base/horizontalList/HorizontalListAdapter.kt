package com.example.selfhostedcloudstorage.ui.listView.base.horizontalList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.selfhostedcloudstorage.R
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryItem
import com.example.selfhostedcloudstorage.restapi.service.NodeRepository

class HorizontalListAdapter : RecyclerView.Adapter<HorizontalListAdapter.HorizontalViewHolder>() {

    private var itemList: List<DirectoryItem> = listOf()
    private var nodeRepository = NodeRepository.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.horizontal_list_item, parent, false)
        return HorizontalViewHolder(view)
    }

    override fun onBindViewHolder(holder: HorizontalViewHolder, position: Int) {
        val item = itemList[position]
        holder.itemText.text = item.name

        holder.itemView.setOnClickListener {
            nodeRepository.readNode(item.path)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun updateList(newItemList: List<DirectoryItem>) {
        itemList = newItemList
        notifyDataSetChanged()
    }

    inner class HorizontalViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView){

        val itemText: TextView = itemView.findViewById(R.id.horizontalItemText)

    }
}
