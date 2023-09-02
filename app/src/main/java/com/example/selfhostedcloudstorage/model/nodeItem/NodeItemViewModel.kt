package com.example.selfhostedcloudstorage.model.nodeItem

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import com.example.selfhostedcloudstorage.model.FileType

class NodeItemViewModel(nodeItem: NodeItem) : ViewModel() {
    var name: String = if (nodeItem.name.contains('/')) {
        nodeItem.name.substringAfterLast('/')
    } else {
        nodeItem.name
    }
    val description: String = nodeItem.description
    val type: FileType = nodeItem.type as FileType
    val depth: Int = nodeItem.depth
    val path: String = nodeItem.path
    var image: Drawable? = null
        set(value) { field = value }
}
