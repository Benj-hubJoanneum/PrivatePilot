package com.example.selfhostedcloudstorage.model.directoryItem.viewmodel

import android.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryItem

class DirectoryBreadcrumbViewModel(directoryItem: DirectoryItem) : ViewModel() {
    var name: String = directoryItem.name
    val path: String = directoryItem.path
    val color: Int = Color.BLACK
}
