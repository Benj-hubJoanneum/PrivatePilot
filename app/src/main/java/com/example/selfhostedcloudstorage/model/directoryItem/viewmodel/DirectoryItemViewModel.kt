package com.example.selfhostedcloudstorage.model.directoryItem.viewmodel

import androidx.lifecycle.ViewModel
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryItem

class DirectoryItemViewModel(directoryItem: DirectoryItem) : ViewModel() {
    var name: String = directoryItem.name
    val path: String = directoryItem.path
    val depth: Int = directoryItem.depth
}
