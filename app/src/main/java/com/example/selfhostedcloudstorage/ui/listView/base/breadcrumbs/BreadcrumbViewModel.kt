package com.example.selfhostedcloudstorage.ui.listView.base.breadcrumbs

import android.graphics.Color
import android.opengl.Visibility
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfhostedcloudstorage.model.directoryItem.viewmodel.DirectoryBreadcrumbViewModel
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryItem

class BreadcrumbViewModel : ViewModel() {
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

        val home = DirectoryBreadcrumbViewModel(DirectoryItem("HOME", ""))
        home.divider = View.INVISIBLE
        list.add(home)

        if (list.size == 1)
            home.name = ""
        else
            list.first().color = Color.rgb(51, 171, 249)//(125,162,0,255)

        _itemList.postValue(list.reversed())
    }
}
