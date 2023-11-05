package com.example.selfhostedcloudstorage.ui.navView

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryItem
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryItemViewModel
import com.example.selfhostedcloudstorage.restapi.service.NodeRepository

class NavViewModel() : ViewModel() {
    private val _selectedFolder = MutableLiveData<DirectoryItemViewModel?>()

    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
    val text: LiveData<String> = _text
    private val _itemList = MutableLiveData<List<DirectoryItemViewModel>>()
    val itemList: LiveData<List<DirectoryItemViewModel>> = _itemList

    private val nodeRepository = NodeRepository.getInstance()

    init {
        setSelectedFolder(_itemList.value?.firstOrNull()) // Corrected function name here
    }

    fun loadFolderList(directoryList: MutableSet<DirectoryItem>) {
        try {
            directoryList.let { list ->
                list.sortedWith(compareBy { it.parentFolder })
                    .map {
                        DirectoryItemViewModel(it)
                        it.depth = it.path.count { it == '/' } - 1
                    }

                _itemList.postValue(list.map { DirectoryItemViewModel(it) })
            }
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error loading folders: ${e.message}")
        }
    }

    fun setSelectedFolder(folder: DirectoryItemViewModel?) {
        if (folder != null) {
            _selectedFolder.value = folder
            nodeRepository.readNode(folder.path)
        }
    }

    fun setSelectedFolder(path: String) {
        val folder = itemList.value?.find { it.name == path }

        if (folder != null)
            _selectedFolder.value = folder
    }
}
