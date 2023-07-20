package com.example.selfhostedcloudstorage.model

interface IFile : ITreeNode {
    var type : Enum<FileType>
}
