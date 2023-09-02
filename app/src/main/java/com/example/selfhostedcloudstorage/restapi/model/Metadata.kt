package com.example.selfhostedcloudstorage.restapi.model

class Metadata(
    override val last_modified: Double,
    override val name: String,
    override val size: Int,
    val type: String
) : IMetadata {
}