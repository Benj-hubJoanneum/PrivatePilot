package com.example.selfhostedcloudstorage.model.fileItem

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import com.example.selfhostedcloudstorage.model.FileType

class FileItemViewModel(fileItem: FileItem) : ViewModel() {
    var name: String = if (fileItem.name.contains('/')) {
        fileItem.name.substringAfterLast('/')
    } else {
        fileItem.name
    }
    val description: String = fileItem.description
    val type: FileType = fileItem.type as FileType
    val depth: Int = fileItem.depth
    val path: String = fileItem.path
    var image: Drawable? = null
        set(value) { field = value }
}
