package com.chrizz.bimdog

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var toggle: ActionBarDrawerToggle? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val navController = Navigation.findNavController(this, R.id.navHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawer_layout)

        toggle = object : ActionBarDrawerToggle(this, drawer_layout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            override fun onDrawerStateChanged(newState: Int) {
                super.onDrawerStateChanged(newState)
                toggle!!.syncState()
            }
        }
        drawer_layout.addDrawerListener(toggle!!)
        toggle!!.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.i("MainActivity", "onOptionsItemSelected")
        val navID = Navigation.findNavController(this, R.id.navHostFragment).currentDestination!!.id
        return if (navID == R.id.stopListFragment) {
            toggle!!.onOptionsItemSelected(item)
        } else super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        Log.i("MainActivity", "onBackPressed")
        if (drawer_layout!!.isDrawerOpen(GravityCompat.START)) {
            drawer_layout!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        Log.i("MainActivity", "onSupportNavigateUp")
        return Navigation.findNavController(this, R.id.navHostFragment).navigateUp()
    }

}
