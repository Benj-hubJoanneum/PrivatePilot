package com.example.selfhostedcloudstorage.model.nodeItem.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfhostedcloudstorage.model.directoryItem.viewmodel.DirectoryBreadcrumbViewModel
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryItem

class HorizontalListViewModel : ViewModel() {
    private val _itemList = MutableLiveData<List<DirectoryBreadcrumbViewModel>>()
    val itemList: LiveData<List<DirectoryBreadcrumbViewModel>> = _itemList

    fun loadList(str: String){
        val list = mutableListOf<DirectoryBreadcrumbViewModel>()
        var path = str
        var name : String

        while (path.isNotEmpty()) {
            name = path.substringAfterLast('/')
            list.add(DirectoryBreadcrumbViewModel(DirectoryItem(name, path)))
            path = path.substringBeforeLast('/')
        }

        list.add(DirectoryBreadcrumbViewModel(DirectoryItem("HOME", "")))
        _itemList.postValue(list.reversed())
    }
}
