package com.example.selfhostedcloudstorage.ui.gridView

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfhostedcloudstorage.service.MockService
import com.example.selfhostedcloudstorage.service.NodesListener
import com.example.selfhostedcloudstorage.model.fileItem.FileItem
import com.example.selfhostedcloudstorage.model.fileItem.FileItemViewModel

class GridViewModel : ViewModel(), NodesListener {

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