package com.example.selfhostedcloudstorage.model.directoryItem

import android.graphics.Color
import androidx.lifecycle.ViewModel

class DirectoryBreadcrumbViewModel(directoryItem: DirectoryItem) : ViewModel() {
    var name: String = directoryItem.name
    val path: String = directoryItem.path
    val color: Int = Color.BLACK
}
