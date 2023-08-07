package com.example.selfhostedcloudstorage.model.directoryItem

import com.example.selfhostedcloudstorage.model.IDirectory
import com.example.selfhostedcloudstorage.model.ITreeNode

data class DirectoryItem(
    override var name: String,
    override var depth: Int = 0,
    override var parentFolder: IDirectory? = null,
    override var path: String = name
) : IDirectory, ITreeNode {
}
