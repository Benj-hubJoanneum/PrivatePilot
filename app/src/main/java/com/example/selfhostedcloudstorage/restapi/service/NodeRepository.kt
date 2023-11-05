package com.example.selfhostedcloudstorage.restapi.service

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: NodeRepository().also { instance = it }
            }
    }

    //collections
    private var controllerNode = ControllerSocket(this, this)
    var selectedFileUri: Uri? = null

    internal var pointer: String = ""
    private val _directoryPointer = MutableLiveData<String>()
    val directoryPointer: LiveData<String> = _directoryPointer

    private val _directoryList = MutableLiveData<MutableSet<DirectoryItem>>()
    val directoryList: LiveData<MutableSet<DirectoryItem>> = _directoryList

    private var fullFileList: MutableSet<INode> = mutableSetOf()
    private val _displayedList = MutableLiveData<MutableList<INode>>()
    val displayedList: LiveData<MutableList<INode>> = _displayedList

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
        val filteredList = fullFileList.filter { node ->
            (node is NodeItem) && node.name.contains(query, ignoreCase = true)
        }.toMutableList()
        _displayedList.postValue(filteredList)
        _directoryPointer.postValue(query)
    }

    fun undoSearch() {
        _displayedList.postValue(fullFileList.toMutableList())
    }

    fun createNode(file: File) {
        CoroutineScope(Dispatchers.IO).launch {
            val path = _directoryPointer.value ?: ""

            if (path != "")
                controllerNode.createNodes(path, file)
        }
    }

    fun readNode(path: String) {
        CoroutineScope(Dispatchers.IO).launch {
            controllerNode.requestNodes(path)
        }
        _directoryPointer.postValue(path.substringAfterLast('/'))
    }

    fun updateNode(path: String) {

    }
    fun deleteNode(path: String) {
        CoroutineScope(Dispatchers.IO).launch {
            controllerNode.deleteNodes(path)
        }
    }

    fun downloadFile(path: String) {
        CoroutineScope(Dispatchers.IO).launch {
            controllerNode.downloadFile(path)
        }
    }

    private fun directoryListAddByParent(list : MutableSet<DirectoryItem>) {
        val directoryList = _directoryList.value ?: mutableSetOf()

        list -= directoryList
        val newList = directoryList.toMutableList()

        for (newNode in list.sortedByDescending { it.name }){
            val index = directoryList.indexOfFirst { it.path == newNode.parentFolder }
            newList.add(index + 1, newNode)
        }
        _directoryList.postValue(newList.toMutableSet())
    }

    private fun displayListSorting(){
        val sortedList = fullFileList
            .sortedWith(compareBy(
                { it.type != FileType.FOLDER },
                { it.name }))
            .toMutableList()
        _displayedList.postValue(sortedList)
    }

    fun fileExist(url: String, context: Context): Boolean {
        return controllerNode.fileExist(url, context).exists()
    }

    fun openFile(filePath: String) {
        controllerNode.openFile(filePath)
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
        fullFileList = nodeList
        directoryListAddByParent(directoryList)
        displayListSorting()
    }
}
