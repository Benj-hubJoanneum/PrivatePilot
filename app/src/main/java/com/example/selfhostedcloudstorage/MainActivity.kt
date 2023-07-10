package com.example.selfhostedcloudstorage

import android.os.Bundle
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.selfhostedcloudstorage.databinding.ActivityMainBinding
import com.example.selfhostedcloudstorage.mockData.MockService
import com.example.selfhostedcloudstorage.mockData.fileItem.FileItemAdapter
import com.example.selfhostedcloudstorage.mockData.fileItem.FileItemViewModel
import com.example.selfhostedcloudstorage.mockData.fileItem.ItemSelectionListener

class MainActivity : AppCompatActivity(), ItemSelectionListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var selectedCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "This should be 'Add file'- Button", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top-level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_all_files
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val visibility = selectedCount > 0 // Determine visibility based on selected count
        val buttonIds = listOf(
            R.id.menu_open_with,
            R.id.menu_edit,
            R.id.menu_delete,
            R.id.menu_send
        )
        buttonIds.forEach { buttonId ->
            menu?.findItem(buttonId)?.isVisible = visibility
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onItemsSelected(selectedCount: Int) {
        this.selectedCount = selectedCount // Update the selected count
        invalidateOptionsMenu() // Trigger the menu to be updated
    }
}
