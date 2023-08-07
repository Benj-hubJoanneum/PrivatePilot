package com.example.selfhostedcloudstorage.model

interface INode {
    var parentFolder : IDirectory?
    var name : String
    var path : String
}