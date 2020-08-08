package com.kuldeep.bookhub.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.kuldeep.bookhub.fragment.Profile
import com.kuldeep.bookhub.R
import com.kuldeep.bookhub.fragment.AboutFragment
import com.kuldeep.bookhub.fragment.DashboardFragment
import com.kuldeep.bookhub.fragment.FavouritesFragment

class MainActivity : AppCompatActivity() {
    lateinit var drawerLayout:DrawerLayout
    lateinit var toolbar:androidx.appcompat.widget.Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var navigationView: NavigationView
    var previousMenuItem: MenuItem?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout=findViewById(R.id.drawerLayout)
        toolbar=findViewById(R.id.toolbar)
        frameLayout=findViewById(R.id.frameLayout)
        coordinatorLayout=findViewById(R.id.coordinatorLayout)
        navigationView=findViewById(R.id.navigationView)
        setUpToolbar()
        openDashboard()
        val actionBarDrawerToggle=ActionBarDrawerToggle(this@MainActivity,drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {
            if (previousMenuItem != null){
                previousMenuItem?.isChecked=false
            }
            it.isCheckable=true
            it.isChecked=true
            previousMenuItem=it
          when(it.itemId){
              R.id.dashboard ->{
                supportFragmentManager.beginTransaction()
                    openDashboard()
                  drawerLayout.closeDrawers()
              }
              R.id.aboutapp ->{
                  supportFragmentManager.beginTransaction()
                      .replace(
                          R.id.frameLayout,
                          AboutFragment()
                      )

                      .commit()
                  supportActionBar?.title="About"
                  drawerLayout.closeDrawers()
              }
              R.id.profile ->{
                  supportFragmentManager.beginTransaction()
                      .replace(
                          R.id.frameLayout,
                          Profile()
                      )

                      .commit()
                  supportActionBar?.title="Profile"
                  drawerLayout.closeDrawers()
              }
              R.id.favourites ->{
                  supportFragmentManager.beginTransaction()
                      .replace(
                          R.id.frameLayout,
                          FavouritesFragment()
                      )

                      .commit()
                  supportActionBar?.title="Favourites"
                  drawerLayout.closeDrawers()

              }
          }
            return@setNavigationItemSelectedListener true
        }
    }
    fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id= item.itemId
        if (id==android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }
    fun openDashboard(){
        val fragment= DashboardFragment()
        val transaction=supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout,fragment)
        transaction.commit()
        supportActionBar?.title="Dashboard"
        navigationView.setCheckedItem(R.id.dashboard)
    }

    override fun onBackPressed() {
        val frag=supportFragmentManager.findFragmentById(R.id.frameLayout)
        when(frag){
            !is DashboardFragment -> openDashboard()
            else -> super.onBackPressed()
        }
    }
}