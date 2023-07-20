package com.example.selfhostedcloudstorage.model

import com.example.selfhostedcloudstorage.model.fileItem.FileItem

class Directory(override var name: String) : INode, IDirectory {
    override var level: Int = 0
    var files: MutableList<INode> = mutableListOf()
}
