package com.example.selfhostedcloudstorage.model.directoryItem

import androidx.lifecycle.ViewModel

class DirectoryItemViewModel(directoryItem: DirectoryItem) : ViewModel() {
    var name: String = directoryItem.name
    val path: String = directoryItem.path
    val depth: Int = directoryItem.depth
}
