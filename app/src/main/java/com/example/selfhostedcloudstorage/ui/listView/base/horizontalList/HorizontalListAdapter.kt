package com.example.selfhostedcloudstorage.ui.listView.base.horizontalList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.selfhostedcloudstorage.R
import com.example.selfhostedcloudstorage.restapi.service.NodeRepository

class HorizontalListAdapter : RecyclerView.Adapter<HorizontalListAdapter.HorizontalViewHolder>() {

    private var itemList: HashMap<String, String> = hashMapOf()
    private var nodeRepository = NodeRepository.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.horizontal_list_item, parent, false)
        return HorizontalViewHolder(view)
    }

    override fun onBindViewHolder(holder: HorizontalViewHolder, position: Int) {
        val item = itemList.entries.elementAt(position)
        holder.itemText.text = item.value

        holder.itemView.setOnClickListener {
            nodeRepository.readNode(item.key)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun updateList(newItemList: HashMap<String, String>) {
        itemList = newItemList
        notifyDataSetChanged()
    }

    inner class HorizontalViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView){

        val itemText: TextView = itemView.findViewById(R.id.horizontalItemText)

    }
}
