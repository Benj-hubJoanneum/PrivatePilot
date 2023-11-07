package com.example.selfhostedcloudstorage.ui.listView.base.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryItemViewModel

class HorizontalListViewModel : ViewModel() {
    private val _itemList = MutableLiveData<HashMap<String, String>>()
    val itemList: LiveData<HashMap<String, String>> = _itemList

    fun loadList(str: String){
        val keyValuePairs = HashMap<String, String>()
        var path = str
        var name : String

        while (path.isNotEmpty()) {
            name = path.substringAfterLast('/')
            keyValuePairs[path] = name
            path = path.substringBeforeLast('/')
        }

        _itemList.postValue(keyValuePairs)
    }
}
