package com.example.selfhostedcloudstorage.mockData

import com.example.selfhostedcloudstorage.mockData.fileItem.FileItem
import com.example.selfhostedcloudstorage.mockData.model.Directory
import com.example.selfhostedcloudstorage.mockData.model.INode
import com.example.selfhostedcloudstorage.ui.allFiles.AllFilesFragment
import com.example.selfhostedcloudstorage.ui.allFiles.AllFilesViewModel
import org.w3c.dom.Node

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
            FileItem("num1.txt", ""),
            FileItem("num2.xlsx", ""),
            FileItem("num3.pdf", ""),
            FileItem("num4.jpg", ""),
            FileItem("num4.png", "")
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
