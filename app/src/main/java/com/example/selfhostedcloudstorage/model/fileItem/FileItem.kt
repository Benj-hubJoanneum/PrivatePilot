package com.example.selfhostedcloudstorage.model.fileItem

import com.example.selfhostedcloudstorage.model.FileType
import com.example.selfhostedcloudstorage.model.IDirectory
import com.example.selfhostedcloudstorage.model.IFile

data class FileItem(
    override var name: String,
    val description: String = "",
    override var depth: Int = 0,
    override var parentFolder: IDirectory? = null,
    override var path: String = name,
) : IFile, Comparable<FileItem> {

    override var type: Enum<FileType> = setByType()

    private fun setByType(): FileType {
        val extension = name.substringAfterLast('.', "")
        return try {
            FileType.valueOf(extension.uppercase())
        } catch (e: IllegalArgumentException) {
            FileType.DOC
        }
    }

    override fun compareTo(other: FileItem): Int {
        return this.name.compareTo(other.name)
    }
}
