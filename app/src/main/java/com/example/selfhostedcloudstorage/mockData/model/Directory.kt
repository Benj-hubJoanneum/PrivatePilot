package com.example.selfhostedcloudstorage.mockData.model

import com.example.selfhostedcloudstorage.mockData.fileItem.FileItem

class Directory(override var name: String) : INode, IDirectory {
    override var level: Int = 0
    var files: MutableList<INode> = mutableListOf()
}
