package com.example.selfhostedcloudstorage.ui.navView

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryItem
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryItemViewModel

class NavViewModel() : ViewModel() {

    private val _itemList = MutableLiveData<List<DirectoryItemViewModel>>()
    val itemList: LiveData<List<DirectoryItemViewModel>> = _itemList

    private val _selectedFolder = MutableLiveData<DirectoryItemViewModel?>()
    val selectedFolder: LiveData<DirectoryItemViewModel?> = _selectedFolder

    fun loadFolderList(directoryList: MutableSet<DirectoryItem>) {
        try {
            directoryList.let { list ->
                list.sortedWith(compareBy { it.parentFolder })
                    .map {
                        DirectoryItemViewModel(it)
                        it.depth = it.path.count { it == '/' } - 1
                    }

                _itemList.postValue(list.map { DirectoryItemViewModel(it) })
            }
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error loading folders: ${e.message}")
        }
    }

    fun setSelectedFolder(path: String) {
        try {
            val folder = _itemList.value?.find { it.name == path }
            if (folder != null)
                _selectedFolder.postValue(folder)
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error selecting folder: ${e.message}")
        }
    }

    fun setSelectedFolder(folder: DirectoryItemViewModel) {
        try {
            _selectedFolder.postValue(folder)
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error selecting folder: ${e.message}")
        }
    }
}
