package com.example.selfhostedcloudstorage.ui.treeView

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryItem
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryItemViewModel
import com.example.selfhostedcloudstorage.model.nodeItem.NodeItem
import com.example.selfhostedcloudstorage.restapi.service.NodeRepository

class TreeViewModel : ViewModel(), NodeRepository.RepositoryListener  {

    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
    val text: LiveData<String> = _text
    private val _itemList = MutableLiveData<List<DirectoryItemViewModel>>()
    val itemList: LiveData<List<DirectoryItemViewModel>> = _itemList

    private val nodeRepository = NodeRepository.getInstance()

    init {
        loadFolderList()
        nodeRepository.addListener(this)
    }

    private fun loadFolderList() {
        try {
            val itemList = mutableListOf<DirectoryItemViewModel>()
            val folderSet = mutableSetOf<String>()

            for (fileItem in nodeRepository.displayedList.filterIsInstance<NodeItem>()) {
                val filePathSegments = fileItem.name.split("/")
                var currentPath = ""

                for ((index, folderName) in filePathSegments.withIndex()) {
                    if (index < filePathSegments.size - 1) { // Ignore the last segment (file name)
                        if (index == 0 && folderName.isEmpty()) {
                            currentPath = "/"
                        } else {
                            currentPath += "/$folderName"
                        }

                        // Check if this folder is unique and hasn't been added before
                        if (currentPath !in folderSet) {
                            folderSet.add(currentPath)
                            val depth = currentPath.count { it == '/' }
                            itemList.add(DirectoryItemViewModel(DirectoryItem(currentPath, "", depth - 1)))
                        }
                    }
                }
            }

            _itemList.value = itemList
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error loading folders: ${e.message}")
        }
    }


    override fun onSourceChanged() {
        loadFolderList()
    }
}
