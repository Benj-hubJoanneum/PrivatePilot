package com.example.selfhostedcloudstorage.ui.listView.viewModel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfhostedcloudstorage.restapi.service.NodeRepository
import com.example.selfhostedcloudstorage.model.nodeItem.NodeItem
import com.example.selfhostedcloudstorage.model.nodeItem.NodeItemViewModel
import com.example.selfhostedcloudstorage.restapi.service.RepositoryListener

class RecyclerViewModel : ViewModel(), RepositoryListener {

    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
    val text: LiveData<String> = _text
    private val _itemList = MutableLiveData<List<NodeItemViewModel>>()
    val itemList: LiveData<List<NodeItemViewModel>> = _itemList

    private val nodeRepository = NodeRepository.getInstance()

    init {
        loadFileList()
        nodeRepository.addListener(this)
    }

    private fun loadFileList() {
        try {
            val fileList = nodeRepository.displayedList

            _itemList.postValue(fileList.map { NodeItemViewModel(it as NodeItem) })
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error loading files: ${e.message}")
        }
    }

    override fun onSourceChanged() {
        loadFileList()
    }
}
