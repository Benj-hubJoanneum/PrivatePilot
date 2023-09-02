package com.example.selfhostedcloudstorage.restapi.controller

import com.example.selfhostedcloudstorage.model.INode
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryItem
import com.example.selfhostedcloudstorage.model.nodeItem.NodeItem
import com.example.selfhostedcloudstorage.restapi.webAccess.WebAccess
import com.example.selfhostedcloudstorage.restapi.model.MetadataResponse
import com.google.gson.Gson

class ControllerNode {
    private val webAccess: WebAccess = WebAccess.getInstance()

    var directoryList = mutableSetOf<DirectoryItem>()
    var _nodeList = mutableSetOf<INode>()

    private var listener: ControllerListener? = null

    suspend fun readNodes(path: String) {
        val nodes = webAccess.readNodes(path)
        if (!nodes.isNullOrEmpty()) {
            val nodeList = nodes[0].parseItemsFromResponse()
            directoryList.addAll(nodeList.items.filter { it.type == "folder" }.map {
                DirectoryItem(it.name, "$path/${it.name}")
            })
            _nodeList = nodeList.items.map { NodeItem(it.name, "$path/${it.name}") }.toMutableSet()
            listener?.onSourceChanged()
        }
    }

    private fun String.parseItemsFromResponse(): MetadataResponse {
        return try {
            Gson().fromJson(this, MetadataResponse::class.java)
        } catch (e: Exception) {
            MetadataResponse(listOf())
        }
    }

    fun addListener(controllerListener: ControllerListener) {
        listener = controllerListener
    }
}
