package com.example.selfhostedcloudstorage.restapi.service

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.selfhostedcloudstorage.model.FileType
import com.example.selfhostedcloudstorage.model.nodeItem.NodeItem
import com.example.selfhostedcloudstorage.model.INode
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryItem
import com.example.selfhostedcloudstorage.restapi.controller.ControllerListener
import com.example.selfhostedcloudstorage.restapi.controller.ControllerNode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class NodeRepository private constructor() : ControllerListener {
    companion object {
        @Volatile
        private var instance: NodeRepository? = null

        fun getInstance(): NodeRepository =
            instance ?: synchronized(this) {
                instance ?: NodeRepository().also { instance = it }
            }
    }

    private var fullFileList: MutableSet<INode> = mutableSetOf()
    internal var directoryList: MutableSet<DirectoryItem> = mutableSetOf()
    internal var displayedList: MutableList<INode> = mutableListOf()
    private var controllerNode = ControllerNode()
    private var listeners: MutableSet<RepositoryListener> = mutableSetOf()

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

    fun addListener(repositoryListener: RepositoryListener) {
        listeners.add(repositoryListener)
    }

    fun createNode(path: String, file: File) {
        CoroutineScope(Dispatchers.IO).launch {
            controllerNode.createNodes(path, file)
            onSourceChanged()
        }
    }

    fun createNode(path: String, inputStream: InputStream) {
        CoroutineScope(Dispatchers.IO).launch {
            controllerNode.createNodes(path, inputStream)
            onSourceChanged()
        }
    }

    fun readNode(path: String) {
        CoroutineScope(Dispatchers.IO).launch {
            controllerNode.readNodes(path)
            onSourceChanged()
        }
    }

    fun updateNode(path: String) {

    }
    fun deleteNode(path: String) {
        CoroutineScope(Dispatchers.IO).launch {
            controllerNode.deleteNodes(path)
            onSourceChanged()
        }
    }

    fun downloadFile(context: Context, path: String) {
        CoroutineScope(Dispatchers.IO).launch {
            controllerNode.downloadFile(path, context)
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

    fun fileExist(context: Context, url: String): Boolean {
        return controllerNode.fileExist(context, url).exists()
    }

    fun openFile(context: Context, filePath: String) {
        controllerNode.openFile(context, filePath)
    }
}
