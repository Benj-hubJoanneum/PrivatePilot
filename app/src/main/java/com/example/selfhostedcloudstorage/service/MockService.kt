package com.example.selfhostedcloudstorage.service

import com.example.selfhostedcloudstorage.model.fileItem.FileItem
import com.example.selfhostedcloudstorage.model.INode

class MockService private constructor() {
    companion object {
        @Volatile
        private var instance: MockService? = null

        fun getInstance(): MockService =
            instance ?: synchronized(this) {
                instance ?: MockService().also { instance = it }
            }
    }

    var displayedList: MutableList<INode> = mutableListOf()
    var fullFileList: MutableList<INode> = mutableListOf()
    private var listener: NodesListener? = null

    init {
        fullFileList = mutableListOf(
            FileItem("/num1.txt", ""),
            FileItem("/num2.xlsx", ""),
            FileItem("/folder/num3.pdf", ""),
            FileItem("/folder/folder(2)/num4.jpg", ""),
            FileItem("/folder/folder(2)/num4.png", "")
        )

        displayedList = fullFileList
    }

    fun onSearchQuery(query: String) {
        if (query.isNullOrBlank())
            undoSearch()
        displayedList = fullFileList.filterIsInstance<FileItem>().filter { fileItem ->
            fileItem.name.contains(query, ignoreCase = true)
        }.toMutableList()
        listener?.onSourceChanged()
    }

    fun undoSearch(){
        displayedList = fullFileList
        listener?.onSourceChanged()
    }

    fun addListener(nodesListener: NodesListener) {
        listener = nodesListener
    }
}
