package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = DatabaseHelper(this)

        val usernameInput: EditText = findViewById(R.id.editUsername)
        val passwordInput: EditText = findViewById(R.id.editPassword)
        val loginBtn: Button = findViewById(R.id.btnLogin)
        val registerBtn: Button = findViewById(R.id.btnRegister)

        registerBtn.setOnClickListener {
            val user = usernameInput.text.toString()
            val pass = passwordInput.text.toString()
            val role = "User" // Everyone registers as a standard User now

            if (user.isNotEmpty() && pass.isNotEmpty()) {
                val result = db.addUser(user, pass, role)
                if (result != -1L) {
                    Toast.makeText(this, "Account Created! Please Login.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Registration failed or username taken", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        loginBtn.setOnClickListener {
            val user = usernameInput.text.toString()
            val pass = passwordInput.text.toString()

            // --- SECRET ADMIN LOGIN BACKDOOR ---
            if (user == "admin" && pass == "admin123") {
                Toast.makeText(this, "Admin Access Granted", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, DashboardActivity::class.java)
                intent.putExtra("USERNAME", "Admin")
                intent.putExtra("ROLE", "Admin")
                startActivity(intent)
                return@setOnClickListener
            }
            // -----------------------------------

            val userRole = db.getUserRole(user, pass)

            if (userRole != null) {
                Toast.makeText(this, "Welcome $user!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, DashboardActivity::class.java)
                intent.putExtra("USERNAME", user)
                intent.putExtra("ROLE", userRole)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }
}