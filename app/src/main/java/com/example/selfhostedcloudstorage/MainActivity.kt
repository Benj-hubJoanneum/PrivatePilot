package com.example.selfhostedcloudstorage

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.widget.ImageView
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
import com.example.selfhostedcloudstorage.ui.navView.NavModel
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var actionMode: ActionMode? = null
    private val nodeRepository = NodeRepository.getInstance() // omit this
    val openFileLauncher: ActivityResultLauncher<Intent> =
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
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        val drawerRecyclerView = binding.navView.findViewById<RecyclerView>(R.id.drawer_recyclerview)
        drawerRecyclerView.layoutManager = LinearLayoutManager(this)

        val imageView: ImageView = findViewById(R.id.imageView)

        imageView.setOnClickListener {
            val currentFragmentId = navController.currentDestination?.id
            val newFragmentId = if (currentFragmentId == R.id.nav_listview) {
                R.id.nav_gridview
            } else {
                R.id.nav_listview
            }
            navController.navigate(newFragmentId)
            drawerLayout.closeDrawers()
        }

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_listview, R.id.nav_gridview
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val navModel = ViewModelProvider(this)[NavModel::class.java]
        val navAdapter = NavAdapter(navModel.itemList.value ?: emptyList(), navModel)
        drawerRecyclerView.adapter = navAdapter

        navModel.itemList.observe(this) { items ->
            navAdapter.updateList(items)
        }

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
            nodeRepository.undoSearch()
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