package com.example.selfhostedcloudstorage.model.nodeItem

import com.example.selfhostedcloudstorage.model.FileType
import com.example.selfhostedcloudstorage.model.IFile
import com.example.selfhostedcloudstorage.restapi.model.IMetadata

data class NodeItem(
    override var name: String,
    override var path: String = name,
    val description: String = "",
    override var depth: Int = 0,
    override val last_modified: Double = 0.0,
    override val size: Int = 0,
) : IFile, IMetadata, Comparable<NodeItem> {

    override var type: Enum<FileType> = setByType()
    override var parentFolder: String = setParent()

    private fun setByType(): FileType {
        val extension = name.substringAfterLast('.', "")
        return try {
            FileType.valueOf(extension.uppercase())
        } catch (e: IllegalArgumentException) {
            FileType.DOC
        }
    }

    private fun setParent(): String {
        return path.split('/')[0]
    }

    override fun compareTo(other: NodeItem): Int {
        return this.path.compareTo(other.path)
    }
}
