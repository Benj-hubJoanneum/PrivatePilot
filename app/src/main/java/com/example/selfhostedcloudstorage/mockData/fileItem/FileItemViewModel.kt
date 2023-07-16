package com.example.selfhostedcloudstorage.mockData.fileItem

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import com.example.selfhostedcloudstorage.mockData.model.FileType

class FileItemViewModel(fileItem: FileItem) : ViewModel() {
    val name: String = fileItem.name
    val description: String = fileItem.description
    val type: FileType = fileItem.type as FileType
    var image: Drawable? = null
        set(value) { field = value }
}
