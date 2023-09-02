package com.example.selfhostedcloudstorage.ui.listView

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfhostedcloudstorage.model.FileType
import com.example.selfhostedcloudstorage.restapi.service.ApiService
import com.example.selfhostedcloudstorage.model.nodeItem.NodeItem
import com.example.selfhostedcloudstorage.model.nodeItem.NodeItemViewModel
import com.example.selfhostedcloudstorage.restapi.service.ApiListener

class ListViewModel : ViewModel(), ApiListener {

    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
    val text: LiveData<String> = _text
    private val _itemList = MutableLiveData<List<NodeItemViewModel>>()
    val itemList: LiveData<List<NodeItemViewModel>> = _itemList

    private val apiService = ApiService.getInstance()

    init {
        loadFileList()
        apiService.addListener(this)
    }

    private fun loadFileList() {
        try {
            val itemList = apiService.displayedList
            _itemList.value = itemList.map { fileItem: Any ->
                NodeItemViewModel(fileItem as NodeItem)
            }.sortedWith(compareBy(
                { it.type != FileType.FOLDER },
                { it.path }))
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error loading files: ${e.message}")
        }
    }

    override fun onSourceChanged() {
        loadFileList()
    }
}
