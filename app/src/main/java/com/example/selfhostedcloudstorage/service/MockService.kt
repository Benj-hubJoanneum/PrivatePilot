package com.example.selfhostedcloudstorage.service

import com.example.selfhostedcloudstorage.model.fileItem.FileItem
import com.example.selfhostedcloudstorage.model.Directory
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

    val documentsDirectory: Directory
    private var fullFileList: MutableList<INode> = mutableListOf()
    private var listener: NodesListener? = null

    init {
        fullFileList = mutableListOf(
            FileItem("folder/num1.txt", ""),
            FileItem("folder/num2.xlsx", ""),
            FileItem("folder(2)/num3.pdf", ""),
            FileItem("folder(2)/num4.jpg", ""),
            FileItem("folder(2)/num4.png", "")
        )

        documentsDirectory = Directory("Documents")
        documentsDirectory.files = fullFileList
    }

    fun onSearchQuery(query: String) {
        if (query.isNullOrBlank())
            undoSearch()
        documentsDirectory.files = fullFileList.filterIsInstance<FileItem>().filter { fileItem ->
            fileItem.name.contains(query, ignoreCase = true)
        }.toMutableList()
        listener?.onSourceChanged()
    }

    fun undoSearch(){
        documentsDirectory.files = fullFileList
        listener?.onSourceChanged()
    }

    fun addListener(nodesListener: NodesListener) {
        listener = nodesListener
    }
}
