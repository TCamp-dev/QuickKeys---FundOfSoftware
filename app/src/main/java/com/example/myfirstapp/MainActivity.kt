package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        val db = DatabaseHelper(this)
        val usernameInput: EditText = findViewById(R.id.editUsername)
        val passwordInput: EditText = findViewById(R.id.editPassword)

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            val user = usernameInput.text.toString().trim()
            val pass = passwordInput.text.toString().trim()
            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Admin backdoor
            if (user == "admin" && pass == "admin123") {
                startDashboard(-1, "Admin", "Admin")
                return@setOnClickListener
            }
            val loggedUser = db.getUser(user, pass)
            if (loggedUser != null) {
                startDashboard(loggedUser.id, loggedUser.username, loggedUser.role)
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnRegister).setOnClickListener {
            val user = usernameInput.text.toString().trim()
            val pass = passwordInput.text.toString().trim()
            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pass.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val result = db.addUser(user, pass, "User")
            if (result != -1L) {
                Toast.makeText(this, "Account created! Please log in.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startDashboard(id: Int, username: String, role: String) {
        startActivity(Intent(this, DashboardActivity::class.java).apply {
            putExtra("USER_ID", id)
            putExtra("USERNAME", username)
            putExtra("ROLE", role)
        })
    }
}