package com.example.selfhostedcloudstorage.restapi.model

class Metadata(
    override val name: String,
    val type: String,
    override val size: Int,
    override val last_modified: Double
) : IMetadata {
}