package com.example.selfhostedcloudstorage.restapi.model

import com.example.selfhostedcloudstorage.model.FileType

interface IMetadata {
    val last_modified: Double
    val name: String
    val size: Int
}