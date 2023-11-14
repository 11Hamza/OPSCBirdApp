package com.test.userlocation

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class LoginPage : AppCompatActivity() {


    private val database = FirebaseDatabase.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)





        setupActivityLink()
        loginButton()


    }



    //login button

    private fun loginButton() {
        val username = findViewById<EditText>(R.id.edt_username)
        val password = findViewById<EditText>(R.id.edt_password)
        val loginBtn = findViewById<Button>(R.id.btn_login)


        //when the login button is clicked these lines of code run
        loginBtn.setOnClickListener {


            //if the username and the password is not entered it shows this error message
            if (username.text.isEmpty() && password.text.isEmpty()) {
                Toast.makeText(this, "Username and Password cannot be empty", Toast.LENGTH_SHORT)
                    .show()


                // if either the username or password is not entered this message shows
            } else if (username.text.isEmpty() || password.text.isEmpty()) {
                Toast.makeText(this, "Please fill in login details", Toast.LENGTH_SHORT).show()


            } else {
                //else it would run this can look for the user if they are in the database
                val auth: FirebaseAuth = Firebase.auth
                val enteredUsername = username.text.toString()
                val enteredPassword = password.text.toString()


                //looks for the user in the database
                auth.signInWithEmailAndPassword(enteredUsername, enteredPassword)
                    .addOnCompleteListener(this) { task ->

                        //if the user is successful
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_LONG).show()
                            username.setText("") //clears the username edit text
                            password.setText("") //clears the password edit text


                            //this pushes the user id to the next activity
                            val user: FirebaseUser? = auth.currentUser
                            val uuid: String? = user?.uid
                            if (uuid != null) {
                                val switchActivityIntent = Intent(this, HomePage::class.java)
                                switchActivityIntent.putExtra("UUID_EXTRA", uuid)
                                startActivity(switchActivityIntent)
                                finish() // prevents the user from coming back to the login page
                            }


                        } else {
                            //if the user is not found
                            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show()
                            username.setText("") //clears the username edit text
                            password.setText("") //clears the password edit text

                        }
                    }
                    //displays the error if the user is not on the database
                    .addOnFailureListener(this) { exception ->
                        Toast.makeText(this@LoginPage, exception.message, Toast.LENGTH_LONG)
                            .show()
                    }
            }
        }
    }

// register hyper link text

    fun setupActivityLink() {
        val linkTextView = findViewById<TextView>(R.id.txt_signUp)
        linkTextView.setTextColor(Color.BLUE) // sets the text to blue

        //when the hyperlink text is clicked
        linkTextView.setOnClickListener {
            val switchActivityIntent =
                Intent(this, RegisterPage::class.java) // navigates to to the register page
            startActivity(switchActivityIntent)
        }
    }

}