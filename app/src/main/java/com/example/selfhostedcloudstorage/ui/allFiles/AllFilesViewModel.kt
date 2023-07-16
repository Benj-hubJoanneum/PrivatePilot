package com.example.selfhostedcloudstorage.ui.allFiles

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfhostedcloudstorage.mockData.MockService
import com.example.selfhostedcloudstorage.mockData.fileItem.FileItemViewModel

class AllFilesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
    val text: LiveData<String> = _text

    private val _fileList = MutableLiveData<List<FileItemViewModel>>()
    val fileList: LiveData<List<FileItemViewModel>> = _fileList

    init {
        // Get data source
        val mockService = MockService()
        val documentsDirectory = mockService.documentsDirectory

        try {
            _fileList.value = documentsDirectory.files.map { fileItem ->
                FileItemViewModel(fileItem)
            }
        } catch (e: Exception) {
            // Handle the load error here
            Log.e(ContentValues.TAG, "Error loading files: ${e.message}")
        }
    }
}
