package com.example.selfhostedcloudstorage.ui.allFiles

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
        val fileListData = documentsDirectory.files.map { FileItemViewModel(it) }
        _fileList.value = fileListData
    }
}