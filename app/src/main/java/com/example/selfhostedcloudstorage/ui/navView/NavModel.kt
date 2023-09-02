package com.example.selfhostedcloudstorage.ui.navView

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryItem
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryItemViewModel
import com.example.selfhostedcloudstorage.restapi.service.ApiListener
import com.example.selfhostedcloudstorage.restapi.service.ApiService

class NavModel : ViewModel(), ApiListener {
    private val _selectedFolder = MutableLiveData<DirectoryItemViewModel?>()

    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
    val text: LiveData<String> = _text
    private val _itemList = MutableLiveData<List<DirectoryItemViewModel>>()
    val itemList: LiveData<List<DirectoryItemViewModel>> = _itemList

    private val apiService = ApiService.getInstance()

    init {
        loadFolderList()
        setSelectedFolder(_itemList.value?.firstOrNull()) // Corrected function name here
        apiService.addListener(this)
    }

    private fun loadFolderList() {
        try {
            val itemList = apiService.directoryList
            itemList.sortedWith(compareBy { it.parentFolder })
                .map { directoryItem ->  directoryItem.depth = directoryItem.path.count { it == '/' } - 1 }


            _itemList.value = itemList.map { directoryItem: Any ->
                DirectoryItemViewModel(directoryItem as DirectoryItem)
            } // Then sort lexicographically by path

            _itemList.value!!.forEach { println(it.path) }
        }catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error loading folders: ${e.message}")
        }
    }
    override fun onSourceChanged() {
        loadFolderList()
    }
    fun setSelectedFolder(folder: DirectoryItemViewModel?) { // Corrected function name here
        if (folder != null) {
            _selectedFolder.value = folder
            apiService.onOpenFolder(folder.path)
        }
    }
}
