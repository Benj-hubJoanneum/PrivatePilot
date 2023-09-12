package com.example.selfhostedcloudstorage.ui.listView.viewModel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfhostedcloudstorage.restapi.service.ApiService
import com.example.selfhostedcloudstorage.model.nodeItem.NodeItem
import com.example.selfhostedcloudstorage.model.nodeItem.NodeItemViewModel
import com.example.selfhostedcloudstorage.restapi.service.ApiListener

class RecyclerViewModel : ViewModel(), ApiListener {

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
            val fileList = apiService.displayedList

            _itemList.postValue(fileList.map { NodeItemViewModel(it as NodeItem) })
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error loading files: ${e.message}")
        }
    }

    override fun onSourceChanged() {
        loadFileList()
    }
}
