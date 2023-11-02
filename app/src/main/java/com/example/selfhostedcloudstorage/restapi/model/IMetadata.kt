package com.example.selfhostedcloudstorage.restapi.model

interface IMetadata {
    val last_modified: Double
    val name: String
    val size: Int
}