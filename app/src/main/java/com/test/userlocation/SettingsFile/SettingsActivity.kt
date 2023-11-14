package com.test.userlocation.SettingsFile

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.test.userlocation.HomePage
import com.test.userlocation.LoginPage
import com.test.userlocation.MainActivity
import com.test.userlocation.R

class SettingsActivity : AppCompatActivity() {

    private lateinit var btnChangeRadius: Button
    private lateinit var textViewRadiusUnit: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var signout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences("user_settings", Context.MODE_PRIVATE)


        btnChangeRadius = findViewById(R.id.btnRadius)
        textViewRadiusUnit = findViewById(R.id.userSetRadius)



        btnChangeRadius.setOnClickListener {
            showDialog()
        }

        logout() //calls the logout method to sign the user out
        setupActivityLink() //calls the setupActivityLink method to take the user to home page


    }

    private fun logout(){

        signout = findViewById(R.id.btnSignOut)
        val auth: FirebaseAuth = Firebase.auth


        signout.setOnClickListener {
            // Check if a user is currently signed in
            if (auth.currentUser != null) {
                // Sign out the user
                Firebase.auth.signOut()
                // Sign-out was successful, go to the login activity
                val intent = Intent(this, LoginPage::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                // Call finish to prevent the user from going back to the main activity
                finish()
            } else {
                // No user was signed in
                Toast.makeText(this, "No user is currently signed in.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.radius_settings)

        val radiusEditText = dialog.findViewById<EditText>(R.id.radiusEditText)  // Assume you have an EditText with this id
        val saveButton = dialog.findViewById<Button>(R.id.saveButton)  // Assume you have a Button with this id

        // In SettingsActivity
        val unitPreference = dialog.findViewById<RadioGroup>(R.id.unit_preference)
        unitPreference.setOnCheckedChangeListener { group, checkedId ->
            val unit = if (checkedId == R.id.kilometers) "km" else "mi"
            val sharedPreferences = getSharedPreferences("user_settings", Context.MODE_PRIVATE)
            sharedPreferences.edit().putString("unit_key", unit).apply()
        }


        // Load and display the previously saved radius value, if any
        val savedRadius = sharedPreferences.getFloat("radius_key", 10.0f)  // Default radius is 10.0 km
        radiusEditText.setText(savedRadius.toString())

        saveButton.setOnClickListener {
            val radiusValue = radiusEditText.text.toString().toFloatOrNull()
            if (radiusValue != null) {
                saveRadiusValue(radiusValue)
                navigateBack()

            } else {
                // Handle invalid input, e.g., show a Toast message
            }
            dialog.dismiss()
        }

        dialog.show()
    }
    private fun navigateBack() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun saveRadiusValue(radius: Float) {
        val editor = sharedPreferences.edit()
        editor.putFloat("radius_key", radius)
        editor.apply()
    }

    fun setupActivityLink() {
        val done = findViewById<TextView>(R.id.hyperlinkText)


        //when the hyperlink text is clicked
        done.setOnClickListener {
            val switchActivityIntent =
                Intent(this, HomePage::class.java) // navigates to to the register page
            startActivity(switchActivityIntent)
        }
    }

}
