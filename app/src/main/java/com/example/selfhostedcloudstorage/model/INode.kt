package com.example.selfhostedcloudstorage.model

import android.graphics.Bitmap

interface INode {
    var parentFolder : String
    var name : String
    var path : String
    var type : FileType
}