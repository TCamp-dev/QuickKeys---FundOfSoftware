package com.example.myfirstapp

import android.os.Bundle
import android.widget.*
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
        load()

        listView.setOnItemLongClickListener { _, _, position, _ ->
            val user = db.getAllUsers()[position]
            AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Permanently delete '${user.username}'?")
                .setPositiveButton("Delete") { _, _ ->
                    db.deleteUser(user.id)
                    Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show()
                    load()
                }
                .setNegativeButton("Cancel", null).show()
            true
        }
    }

    private fun load() {
        val items = db.getAllUsers().map { "👤  ${it.username}   |   ${it.role}" }
        listView.adapter = ArrayAdapter(this, R.layout.list_item_user, items)
    }
}