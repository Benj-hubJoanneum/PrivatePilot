package com.example.selfhostedcloudstorage.model.fileItem

import com.example.selfhostedcloudstorage.model.FileType
import com.example.selfhostedcloudstorage.model.IFile
import com.example.selfhostedcloudstorage.model.ITreeNode

data class FileItem(override var name: String, val description: String) : IFile, ITreeNode {

    override var type: Enum<FileType> = setByType()
    override var level: Int = 0

    private fun setByType(): FileType {
        val extension = name.substringAfterLast('.', "")
        return try {
            FileType.valueOf(extension.uppercase())
        } catch (e: IllegalArgumentException) {
            FileType.DOC
        }
    }
}
