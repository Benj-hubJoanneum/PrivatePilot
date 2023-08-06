package com.example.selfhostedcloudstorage.ui.listView

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfhostedcloudstorage.service.MockService
import com.example.selfhostedcloudstorage.service.NodesListener
import com.example.selfhostedcloudstorage.model.fileItem.FileItem
import com.example.selfhostedcloudstorage.model.fileItem.FileItemViewModel

class ListViewModel : ViewModel(), NodesListener {

    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
    val text: LiveData<String> = _text
    private val _itemList = MutableLiveData<List<FileItemViewModel>>()
    val itemList: LiveData<List<FileItemViewModel>> = _itemList

    private val mockService = MockService.getInstance()

    init {
        loadFileList()
        mockService.addListener(this)
    }

    private fun loadFileList() {
        try {
            val itemList = mockService.displayedList
            _itemList.value = itemList.map { fileItem: Any ->
                FileItemViewModel(fileItem as FileItem)
            }
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error loading files: ${e.message}")
        }
    }

    override fun onSourceChanged() {
        loadFileList()
    }
}
