package com.test.userlocation

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView



class ObservationPage : AppCompatActivity() {

    private lateinit var addBirdbutton: Button
    private lateinit var adapter: AdpaterClass
    private val birdsList = mutableListOf(
        NewBrd("Sparrow", "Garden"),
        NewBrd("Pigeon", "City"),
        NewBrd("Eagle", "Mountains")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_observation_page)

        adapter = AdpaterClass(birdsList) // Initialize the adapter

        addBirdbutton = findViewById(R.id.button)
        addBirdbutton.setOnClickListener {
            showDialog()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.add_bird)
        dialog.show()

        val addBirdButtonClick = dialog.findViewById<Button>(R.id.finalAddButton)
        val newBirdSight = dialog.findViewById<EditText>(R.id.newBird)
        val locationFound = dialog.findViewById<EditText>(R.id.locationBox)

        addBirdButtonClick.setOnClickListener {
            val newBirdName = newBirdSight.text.toString()
            val newBirdLocation = locationFound.text.toString()

            val newBird = NewBrd(newBirdName, newBirdLocation)
            birdsList.add(newBird)

            adapter.notifyDataSetChanged()

            dialog.dismiss()
        }
    }
}