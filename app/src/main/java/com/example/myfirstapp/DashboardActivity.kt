package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val username = intent.getStringExtra("USERNAME") ?: "User"
        val role = intent.getStringExtra("ROLE") ?: "User"

        val welcomeText: TextView = findViewById(R.id.txtWelcome)
        val roleText: TextView = findViewById(R.id.txtRoleDisplay)
        welcomeText.text = "Welcome, $username!"
        roleText.text = "Account Level: $role"

        val toggleMode: RadioGroup = findViewById(R.id.toggleMode)
        val buyerLayout: LinearLayout = findViewById(R.id.layoutBuyer)
        val sellerLayout: LinearLayout = findViewById(R.id.layoutSeller)
        val adminLayout: LinearLayout = findViewById(R.id.layoutAdmin)

        findViewById<Button>(R.id.btnBrowseCars).setOnClickListener {
            startActivity(Intent(this, BrowseListingsActivity::class.java))
        }

        findViewById<Button>(R.id.btnPostCar).setOnClickListener {
            val intent = Intent(this, AddListingActivity::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnManageListings).setOnClickListener {
            val intent = Intent(this, MyListingsActivity::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        if (role == "User") {
            toggleMode.visibility = View.VISIBLE
            buyerLayout.visibility = View.VISIBLE
            sellerLayout.visibility = View.GONE

            toggleMode.setOnCheckedChangeListener { _, checkedId ->
                if (checkedId == R.id.radioBuyer) {
                    buyerLayout.visibility = View.VISIBLE
                    sellerLayout.visibility = View.GONE
                } else if (checkedId == R.id.radioSeller) {
                    buyerLayout.visibility = View.GONE
                    sellerLayout.visibility = View.VISIBLE
                }
            }
        } else if (role == "Admin") {
            toggleMode.visibility = View.GONE
            adminLayout.visibility = View.VISIBLE

            // NEW: Admin Navigation
            findViewById<Button>(R.id.btnAdminUsers).setOnClickListener {
                startActivity(Intent(this, AdminUsersActivity::class.java))
            }
            findViewById<Button>(R.id.btnAdminListings).setOnClickListener {
                startActivity(Intent(this, AdminListingsActivity::class.java))
            }
        }
    }
}