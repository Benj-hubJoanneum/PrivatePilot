package com.example.selfhostedcloudstorage.model.directoryItem

import androidx.lifecycle.ViewModel

class DirectoryItemViewModel(directoryItem: DirectoryItem) : ViewModel() {
    var name: String = directoryItem.name.substringAfterLast('/', "")
    val depth: Int = directoryItem.depth
}
