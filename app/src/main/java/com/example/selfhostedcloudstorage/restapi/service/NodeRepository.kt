package com.example.selfhostedcloudstorage.restapi.service

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.result.ActivityResultLauncher
import com.example.selfhostedcloudstorage.model.FileType
import com.example.selfhostedcloudstorage.model.nodeItem.NodeItem
import com.example.selfhostedcloudstorage.model.INode
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryItem
import com.example.selfhostedcloudstorage.restapi.controller.ControllerSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class NodeRepository() : ControllerSocket.ControllerCallback {

    companion object {
        @Volatile
        private var instance: NodeRepository? = null

        fun getInstance(): NodeRepository =
            instance ?: synchronized(this) {
                instance ?: NodeRepository().also { instance = it }
            }
    }

    //collections
    private var fullFileList: MutableSet<INode> = mutableSetOf()
    internal var pointer: String = ""
    internal var directoryList: MutableSet<DirectoryItem> = mutableSetOf()
    internal var displayedList: MutableList<INode> = mutableListOf()

    private var controllerNode = ControllerSocket(this, this)
    private var listeners: MutableSet<RepositoryListener> = mutableSetOf() //could save doubles
    var selectedFileUri: Uri? = null


    init {
        CoroutineScope(Dispatchers.IO).launch {
            readNode("")
        }
    }

    fun launchFileSelection(openFileLauncher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"

        openFileLauncher.launch(intent)
    }

    fun onSearchQuery(query: String) {
        displayedList = fullFileList.filter { node ->
            (node is NodeItem) && node.name.contains(query, ignoreCase = true)
        }.toMutableList()
    }

    fun undoSearch() {
        displayedList.clear()
        displayedList.addAll(fullFileList)
    }

    fun addListener(repositoryListener: RepositoryListener) {
        listeners.add(repositoryListener)
    }

    fun createNode(file: File) {
        CoroutineScope(Dispatchers.IO).launch {
            var path = displayedList.first().path//workaround should be swapped as searchfunction will trigger bugs OR flag the the list to disable search
            controllerNode.createNodes(
                path,
                file)
        }
    }

    fun readNode(path: String) {
        CoroutineScope(Dispatchers.IO).launch {
            controllerNode.requestNodes(path)
        }
    }

    fun updateNode(path: String) {

    }
    fun deleteNode(path: String) {
        CoroutineScope(Dispatchers.IO).launch {
            controllerNode.deleteNodes(path)
        }
    }

    fun downloadFile(context: Context, path: String) {
        CoroutineScope(Dispatchers.IO).launch {
            controllerNode.downloadFile(path)
        }
    }

    private fun notifyListeners() {
        listeners.forEach { it.onSourceChanged() }
    }

    private fun directoryListAddByParent(list : MutableSet<DirectoryItem>) {
        list -= directoryList
        val newList = directoryList.toMutableList()

        for (newNode in list.sortedByDescending { it.name }){
            val index = directoryList.indexOfFirst { it.path == newNode.parentFolder }
            newList.add(index + 1, newNode)
        }
        directoryList = newList.toMutableSet()
    }

    private fun displayListSorting(){
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

    fun getThisFile(uri: Uri?, context: Context): File {
        uri ?: return File("")
        val resolver = context.contentResolver

        val inputStream = resolver.openInputStream(uri)
        val file = File(context.cacheDir, resolver.getFileName(uri))

        inputStream.use { input ->
            FileOutputStream(file).use { output ->
                input?.copyTo(output)
            }
        }

        return file
    }

    private fun ContentResolver.getFileName(uri: Uri?): String {
        uri ?: return ""

        val cursor = query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    return it.getString(nameIndex)
                }
            }
        }
        return ""
    }

    override fun onControllerSourceChanged(directoryList : MutableSet<DirectoryItem>, nodeList : MutableSet<INode>) {
        directoryListAddByParent(directoryList)
        fullFileList = nodeList
        displayListSorting()
        notifyListeners()
    }

    interface RepositoryListener {
        fun onSourceChanged()
    }
}
