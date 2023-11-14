package com.test.userlocation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.test.userlocation.SettingsFile.SettingsActivity

class HomePage : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener{


    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)






        val btnExplore = findViewById<ImageButton>(R.id.btn_explore)
        val btnAdd = findViewById<ImageButton>(R.id.btn_add)
        val Settings = findViewById<ImageButton>(R.id.btn_settings)




        Settings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }



        btnExplore.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        btnAdd.setOnClickListener {
            val intent = Intent(this, ObservationPage::class.java)
            startActivity(intent)
        }

        //action bar
        drawerLayout = findViewById(R.id.my_drawer_layout)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)



        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()



        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        val navigationView: NavigationView = findViewById(R.id.TestNav)
        navigationView.setNavigationItemSelectedListener(this)


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.Activity1 -> {
                val accountIntent = Intent(this, HomePage::class.java)
                startActivity(accountIntent)
            }
            R.id.Activity2 -> {
                val settingsIntent = Intent(this, ObservationPage::class.java)
                startActivity(settingsIntent)
            }
            R.id.Activity3 -> {
                val settingsIntent = Intent(this, MainActivity::class.java)
                startActivity(settingsIntent)
            }
            R.id.Activity4 -> {
                val accountIntent = Intent(this, FAQ_Page::class.java) // this will be FAQ
                startActivity(accountIntent)
            }
            R.id.Activity5 -> {
                val accountIntent = Intent(this, HomePage::class.java) //this will be How to use
                startActivity(accountIntent)
            }
            R.id.Activity6 -> {
                val accountIntent = Intent(this, SettingsActivity::class.java)
                startActivity(accountIntent)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}