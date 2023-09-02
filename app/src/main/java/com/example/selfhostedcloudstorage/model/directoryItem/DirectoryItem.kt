package com.example.selfhostedcloudstorage.model.directoryItem

import com.example.selfhostedcloudstorage.model.IDirectory
import com.example.selfhostedcloudstorage.restapi.model.IMetadata
import com.example.selfhostedcloudstorage.model.ITreeNode

data class DirectoryItem(
    override var name: String,
    override var path: String = name,
    override var depth: Int = 0,
    override val last_modified: Double = 0.0,
    override val size: Int = 0
) : IDirectory, ITreeNode, IMetadata, Comparable<DirectoryItem> {

    override var parentFolder: String = setParent()


    private fun setParent(): String {
        val parent = path.split('/')
        return if (parent.size > 1) parent[parent.size - 2] else ""
    }
    override fun compareTo(other: DirectoryItem): Int {
        return this.path.compareTo(other.path)
    }
}
