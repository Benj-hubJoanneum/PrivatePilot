package com.example.selfhostedcloudstorage

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.widget.ImageView
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
import com.example.selfhostedcloudstorage.service.MockService
import com.example.selfhostedcloudstorage.ui.treeView.NavAdapter
import com.example.selfhostedcloudstorage.ui.treeView.NavModel
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var actionMode: ActionMode? = null
    private val mockService = MockService.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(true)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "This should be 'Add file'- Button", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Set up the RecyclerView in the NavigationView
        val drawerRecyclerView = binding.navView.findViewById<RecyclerView>(R.id.drawer_recyclerview)
        drawerRecyclerView.layoutManager = LinearLayoutManager(this)


        val imageView: ImageView = findViewById(R.id.imageView)

        // Set click listener on the ImageView to switch fragments
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
                R.id.nav_listview, R.id.nav_gridview, R.id.nav_treeview
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Initialize NavModel and observe itemList changes to update the adapter
        val navModel = ViewModelProvider(this)[NavModel::class.java]
        val navAdapter = NavAdapter(navModel.itemList.value ?: emptyList())
        drawerRecyclerView.adapter = navAdapter

        // Observe itemList changes to update the adapter
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
                return true //no search until finished
            }
        })

        searchView.setOnCloseListener {
            mockService.undoSearch()
            true // Return true to consume the event
        }

        return true
    }

    private fun onSearchQuery(query: String) {
        mockService.onSearchQuery(query)
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
