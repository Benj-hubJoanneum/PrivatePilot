package com.example.selfhostedcloudstorage.ui.listView.base.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryItem
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryItemViewModel

class HorizontalListViewModel : ViewModel() {
    private val _itemList = MutableLiveData<List<DirectoryItem>>()
    val itemList: LiveData<List<DirectoryItem>> = _itemList

    fun loadList(str: String){
        val list = mutableListOf<DirectoryItem>()
        var path = str
        var name : String

        while (path.isNotEmpty()) {
            name = path.substringAfterLast('/')
            list.add(DirectoryItem(name, path))
            path = path.substringBeforeLast('/')
        }

        _itemList.postValue(list.reversed())
    }
}
