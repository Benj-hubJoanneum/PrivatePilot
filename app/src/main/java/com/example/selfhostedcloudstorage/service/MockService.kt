package com.example.selfhostedcloudstorage.service

import com.example.selfhostedcloudstorage.model.fileItem.FileItem
import com.example.selfhostedcloudstorage.model.INode
import java.util.TreeSet

class MockService private constructor() {
    companion object {
        @Volatile
        private var instance: MockService? = null

        fun getInstance(): MockService =
            instance ?: synchronized(this) {
                instance ?: MockService().also { instance = it }
            }
    }

    private var listener: NodesListener? = null

    // Use TreeSet to maintain sorted order
    private var fullFileList: TreeSet<INode> = TreeSet()

    init {
        // Initialize your fullFileList TreeSet
        fullFileList.addAll(
            listOf(
                FileItem("/num2.xlsx"),
                FileItem("/folder(3)/folder(2)/num5.jpg"),
                FileItem("/folder/num3.pdf"),
                FileItem("/num1.txt"),
                FileItem("/folder(3)/folder(2)/num4.png"),
                FileItem("/folder/folder(2)/num4.jpg"),
                FileItem("/folder(3)/folder(4)/num6.png")
            )
        )
    }

    var displayedList: MutableList<INode> = mutableListOf()

    init {
        displayedList.addAll(fullFileList)
    }

    fun onSearchQuery(query: String) {
        if (query.isNullOrBlank())
            undoSearch()
        displayedList = fullFileList.filter { node ->
            (node is FileItem) && node.name.contains(query, ignoreCase = true)
        }.toMutableList()
        listener?.onSourceChanged()
    }

    fun undoSearch() {
        displayedList.clear()
        displayedList.addAll(fullFileList)
        listener?.onSourceChanged()
    }

    fun addListener(nodesListener: NodesListener) {
        listener = nodesListener
    }
}
