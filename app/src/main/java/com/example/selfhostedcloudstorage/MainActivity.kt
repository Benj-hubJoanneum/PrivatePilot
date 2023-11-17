package com.example.selfhostedcloudstorage

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.selfhostedcloudstorage.databinding.ActivityMainBinding
import com.example.selfhostedcloudstorage.restapi.service.NodeRepository
import com.example.selfhostedcloudstorage.ui.navView.NavAdapter
import com.example.selfhostedcloudstorage.ui.navView.NavViewModel
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var actionMode: ActionMode? = null
    private val nodeRepository = NodeRepository.getInstance()

    private val openFileLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                nodeRepository.selectedFileUri = result.data?.data
                val file = nodeRepository.getThisFile(nodeRepository.selectedFileUri, this)
                nodeRepository.createNode(file)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        binding.appBarMain.fab.setOnClickListener { view ->
            nodeRepository.launchFileSelection(openFileLauncher)
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        val drawerRecyclerView = binding.navView.findViewById<RecyclerView>(R.id.drawer_recyclerview)
        drawerRecyclerView.layoutManager = LinearLayoutManager(this)
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_listview, R.id.nav_gridview
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val navViewModel = ViewModelProvider(this)[NavViewModel::class.java]
        val navAdapter = NavAdapter()
        drawerRecyclerView.adapter = navAdapter

        nodeRepository.directoryList.observe(this) { navViewModel.loadFolderList(it) }
        navViewModel.itemList.observe(this) { navAdapter.updateList(it) }

        nodeRepository.directoryPointer.observe(this) { navViewModel.setSelectedFolder(it) }
        navViewModel.selectedFolder.observe(this) {
            navAdapter.updateSelectedFolder(it)

            var title = it?.name ?: ""
            if (title.isBlank())
                title = "HOME"

            supportActionBar?.title = title
        }

        nodeRepository.readNode("")

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                onSearchQuery(query)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        // Set OnMenuItemClickListener for the search item
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem): Boolean {
                nodeRepository.readNode() // TODO find current path
                return true
            }
        })

        // Set OnQueryTextListener for handling search queries
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    onSearchQuery(query)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        searchView.setOnCloseListener {
            nodeRepository.undoSearch("")
            true
        }

        return true
    }

    private fun onSearchQuery(query: String) {
        nodeRepository.onSearchQuery(query)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onActionModeStarted(mode: ActionMode) {
        super.onActionModeStarted(mode)
        actionMode = mode
        supportActionBar?.hide()
    }

    override fun onActionModeFinished(mode: ActionMode) {
        super.onActionModeFinished(mode)
        actionMode = null
        supportActionBar?.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
