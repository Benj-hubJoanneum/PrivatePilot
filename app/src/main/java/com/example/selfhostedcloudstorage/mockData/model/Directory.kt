package com.example.selfhostedcloudstorage.mockData.model

import com.example.selfhostedcloudstorage.mockData.fileItem.FileItem

class Directory(override var name: String) : INode, IDirectory {
    var files: MutableList<INode> = mutableListOf()
}
