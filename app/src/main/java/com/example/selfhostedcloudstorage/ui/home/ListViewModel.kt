package com.example.selfhostedcloudstorage.ui.home

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfhostedcloudstorage.mockData.MockService
import com.example.selfhostedcloudstorage.mockData.NodesListener
import com.example.selfhostedcloudstorage.mockData.fileItem.FileItem
import com.example.selfhostedcloudstorage.mockData.fileItem.FileItemViewModel

class ListViewModel : ViewModel(), NodesListener {

    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
    val text: LiveData<String> = _text
    private val _fileList = MutableLiveData<List<FileItemViewModel>>()
    val fileList: LiveData<List<FileItemViewModel>> = _fileList

    private val mockService = MockService.getInstance()

    init {
        loadFileList()
        mockService.addListener(this)
    }

    private fun loadFileList() {
        try {
            val fileList = mockService.documentsDirectory.files
            _fileList.value = fileList.map { fileItem: Any ->
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
