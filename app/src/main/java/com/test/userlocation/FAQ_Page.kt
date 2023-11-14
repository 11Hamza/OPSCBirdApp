package com.test.userlocation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.test.userlocation.DataModels.FAQItem
import com.test.userlocation.SettingsFile.SettingsActivity


class FAQ_Page : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener{

    private lateinit var recyclerView: RecyclerView
    private lateinit var faqAdapter: FAQAdapter
    private lateinit var faqList: MutableList<FAQItem>

    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq_page)

        recyclerView = findViewById(R.id.faqRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Populate your list
        faqList = mutableListOf(
            FAQItem("Q: How do I start observing birds with the app?", "A: Begin by creating an account and setting up your profile. Use the map feature to locate nearby birding hotspots and check the species list to see what birds you can expect to find in your area."),
            FAQItem("Q: Can I track the birds I've observed?", "A: Yes, our app features a personal observation log where you can record and track all the birds you've observed, including details like location, date, and time."),
            FAQItem("Q: What should I do if I find an injured bird?", "A: If you find an injured bird, it's best to contact your local wildlife rehabilitation center for advice. Our app provides contact details for centers in your area."),
            FAQItem("Q: Can I contribute to bird conservation through the app?", "A: Absolutely! We have partnerships with conservation organizations. You can participate in citizen science projects and surveys through our app to contribute to bird conservation efforts."),
            FAQItem("Q: How accurate is the bird location data in the app?","A: Our location data is regularly updated and crowd sourced from our user community, making it quite accurate. However, bird movements can be unpredictable, so we always recommend cross-checking with recent community updates."),
            FAQItem("Q: Does the app work in offline mode?","A: Currently the app does not work offline."),
            FAQItem("Q: Are there any guides or tutorials for beginner bird watchers in the app?","A: Yes, we have a range of resources for beginners, including basic bird watching tutorials, tips on how to use binoculars, and guides to understanding bird behaviors.")

        )

        faqAdapter = FAQAdapter(faqList)
        recyclerView.adapter = faqAdapter


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

            R.id.Activity3 -> {
                val settingsIntent = Intent(this, MainActivity::class.java)
                startActivity(settingsIntent)
            }
            R.id.Activity4 -> {
                val accountIntent = Intent(this, FAQ_Page::class.java) // this will be FAQ
                startActivity(accountIntent)
            }
            R.id.Activity5 -> {
                val accountIntent = Intent(this, HowToPage::class.java) //this will be How to use
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