package com.chrizz.bimdog

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var toggle: ActionBarDrawerToggle? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        val navController = Navigation.findNavController(this, R.id.navHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawer_layout)
        navigationView.setNavigationItemSelectedListener(this)

        toggle = object : ActionBarDrawerToggle(this, drawer_layout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            override fun onDrawerStateChanged(newState: Int) {
                super.onDrawerStateChanged(newState)
                toggle?.syncState()
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.i("MainActivity", String.format("onNavigationItemSelected: %d", item.itemId))
        when (item.itemId) {
            R.id.nav_stoplist -> navigate(NavDest.StopList)
            R.id.nav_test -> navigate(NavDest.TestFrag)
            else -> return false
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }


    private enum class NavDest {
        StopList, TestFrag
    }

    private fun navigate(destination: NavDest) {
        val destId: Int
        when (destination) {
            NavDest.StopList -> destId = R.id.stopListFragment
            NavDest.TestFrag -> destId = R.id.testFragment
        }
        Navigation.findNavController(this, R.id.navHostFragment).navigate(destId)
    }

}
