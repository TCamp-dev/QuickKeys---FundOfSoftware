package com.example.myfirstapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class AdminUsersActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_admin_users)

        db = DatabaseHelper(this)
        listView = findViewById(R.id.listViewAllUsers)

        loadUsers()

        listView.setOnItemLongClickListener { _, _, position, _ ->
            val userList = db.getAllUsers()
            val selectedUser = userList[position]

            AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to completely delete the user '${selectedUser.username}'?")
                .setPositiveButton("Delete") { _, _ ->
                    db.deleteUser(selectedUser.id)
                    Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show()
                    loadUsers() // Refresh list
                }
                .setNegativeButton("Cancel", null)
                .show()

            true
        }
    }

    private fun loadUsers() {
        val userList = db.getAllUsers()
        // Format the output string for the simple list
        val displayStrings = userList.map { "Username: ${it.username}\nRole: ${it.role}" }

        // We use Android's built-in simple list layout here to save you from making another XML file
        val adapter = ArrayAdapter(this, R.layout.list_item_user, displayStrings)
        listView.adapter = adapter
    }
}