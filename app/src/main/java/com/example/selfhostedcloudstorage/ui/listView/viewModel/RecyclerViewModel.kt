package com.example.selfhostedcloudstorage.ui.listView.viewModel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfhostedcloudstorage.model.INode
import com.example.selfhostedcloudstorage.restapi.service.NodeRepository
import com.example.selfhostedcloudstorage.model.nodeItem.NodeItem
import com.example.selfhostedcloudstorage.model.nodeItem.NodeItemViewModel

class RecyclerViewModel() : ViewModel(), NodeRepository.NodeRepositoryListener {

    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
    val text: LiveData<String> = _text
    private val _itemList = MutableLiveData<List<NodeItemViewModel>>()
    val itemList: LiveData<List<NodeItemViewModel>> = _itemList
    private val nodeRepository = NodeRepository.getInstance()

    init {
        loadFileList(nodeRepository.displayedList)
        nodeRepository.addNodeListener(this)
    }

    private fun loadFileList(list: MutableList<INode>) {
        try {
            _itemList.postValue(list.map { NodeItemViewModel(it as NodeItem) })
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error loading files: ${e.message}")
        }
    }

    override fun onSourceChanged(list: MutableList<INode>) {
        loadFileList(list)
    }
}
