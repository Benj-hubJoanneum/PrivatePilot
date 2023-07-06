package com.example.selfhostedcloudstorage.mockData

import com.example.selfhostedcloudstorage.mockData.fileItem.FileItem
import com.example.selfhostedcloudstorage.mockData.model.Directory

class MockService {
    val documentsDirectory: Directory

    init {
        val fileList = mutableListOf(
            FileItem("num1.txt", ""),
            FileItem("num2.xlsx", ""),
            FileItem("num3.pdf", ""),
            FileItem("num4.jpg", ""),
            FileItem("num4.png", "")
        )

        documentsDirectory = Directory("Documents")
        documentsDirectory.files = fileList
    }
}
