package com.example.selfhostedcloudstorage.restapi.service

import com.example.selfhostedcloudstorage.model.FileType
import com.example.selfhostedcloudstorage.model.nodeItem.NodeItem
import com.example.selfhostedcloudstorage.model.INode
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryItem
import com.example.selfhostedcloudstorage.restapi.controller.ControllerListener
import com.example.selfhostedcloudstorage.restapi.controller.ControllerNode
import com.example.selfhostedcloudstorage.restapi.model.IMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ApiService private constructor() : ControllerListener {
    companion object {
        @Volatile
        private var instance: ApiService? = null

        fun getInstance(): ApiService =
            instance ?: synchronized(this) {
                instance ?: ApiService().also { instance = it }
            }
    }

    private var fullFileList: MutableSet<INode> = mutableSetOf()
    internal var directoryList: MutableSet<DirectoryItem> = mutableSetOf()
    internal var displayedList: MutableList<INode> = mutableListOf()
    private var controllerNode = ControllerNode()
    private var listeners: MutableSet<ApiListener> = mutableSetOf()

    init {
        controllerNode.addListener(this)
        CoroutineScope(Dispatchers.IO).launch {
            controllerNode.readNodes("")
        }
    }

    fun onSearchQuery(query: String) {
        displayedList = fullFileList.filter { node ->
            (node is NodeItem) && node.name.contains(query, ignoreCase = true)
        }.toMutableList()
        notifyListeners()
    }

    fun undoSearch() {
        displayedList.clear()
        displayedList.addAll(fullFileList)
        notifyListeners()
    }

    fun addListener(apiListener: ApiListener) {
        listeners.add(apiListener)
    }

    fun onOpenFolder(path: String) {
        CoroutineScope(Dispatchers.IO).launch {
            controllerNode.readNodes(path)
            onSourceChanged()
        }
    }

    override fun onSourceChanged() {
        fullFileList = controllerNode._nodeList
        directoryListaddByParent(controllerNode.directoryList)
        displaylistSorting()
        notifyListeners()
    }

    private fun notifyListeners() {
        listeners.forEach { it.onSourceChanged() }
    }

    private fun directoryListaddByParent(list : MutableSet<DirectoryItem>) {
        list -= directoryList
        var newList = directoryList.toMutableList()

        for (newNode in list.sortedByDescending { it.name }){
            val index = directoryList.indexOfFirst { it.path == newNode.parentFolder }
            newList.add(index + 1, newNode)
        }
        directoryList = newList.toMutableSet()
    }

    private fun displaylistSorting(){
        displayedList = fullFileList
            .sortedWith(compareBy(
                { it.type != FileType.FOLDER },
                { it.name }))
            .toMutableList()
    }
}
