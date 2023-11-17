package com.example.selfhostedcloudstorage.model.directoryItem.viewmodel

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryItem

class DirectoryBreadcrumbViewModel(directoryItem: DirectoryItem) : ViewModel() {
    var name: String = directoryItem.name
    val path: String = directoryItem.path
    var color: Int = Color.BLACK
    var divider: Int = View.VISIBLE
}
