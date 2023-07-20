package com.example.selfhostedcloudstorage.mockData.fileItem

import com.example.selfhostedcloudstorage.mockData.model.FileType
import com.example.selfhostedcloudstorage.mockData.model.IFile
import com.example.selfhostedcloudstorage.mockData.model.INode

data class FileItem(override var name: String, val description: String) : IFile, INode {

    override var type: Enum<FileType> = setByType()

    private fun setByType(): FileType {
        val extension = name.substringAfterLast('.', "")
        return try {
            FileType.valueOf(extension.uppercase())
        } catch (e: IllegalArgumentException) {
            FileType.DOC
        }
    }
}