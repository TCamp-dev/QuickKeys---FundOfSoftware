package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    private var userId   = -1
    private var username = ""
    private var role     = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_dashboard)

        userId   = intent.getIntExtra("USER_ID", -1)
        username = intent.getStringExtra("USERNAME") ?: "User"
        role     = intent.getStringExtra("ROLE") ?: "User"

        findViewById<TextView>(R.id.txtWelcome).text     = "Welcome, $username!"
        findViewById<TextView>(R.id.txtRoleDisplay).text = "Account Level: $role"

        val toggleMode   = findViewById<RadioGroup>(R.id.toggleMode)
        val buyerLayout  = findViewById<LinearLayout>(R.id.layoutBuyer)
        val sellerLayout = findViewById<LinearLayout>(R.id.layoutSeller)
        val adminLayout  = findViewById<LinearLayout>(R.id.layoutAdmin)

        // Profile avatar — ImageView now, loads saved URI via ProfileManager
        val btnProfile = findViewById<ImageView>(R.id.btnProfileIcon)
        ProfileManager.loadInto(this, userId, btnProfile)
        btnProfile.setOnClickListener { go(ProfileActivity::class.java) }

        findViewById<Button>(R.id.btnBrowseCars).setOnClickListener { go(BrowseListingsActivity::class.java) }
        findViewById<Button>(R.id.btnPostCar).setOnClickListener { go(AddListingActivity::class.java) }
        findViewById<Button>(R.id.btnManageListings).setOnClickListener { go(MyListingsActivity::class.java) }

        when (role) {
            "User" -> {
                toggleMode.visibility   = View.VISIBLE
                buyerLayout.visibility  = View.VISIBLE
                sellerLayout.visibility = View.GONE
                toggleMode.setOnCheckedChangeListener { _, checkedId ->
                    buyerLayout.visibility  = if (checkedId == R.id.radioBuyer) View.VISIBLE else View.GONE
                    sellerLayout.visibility = if (checkedId == R.id.radioSeller) View.VISIBLE else View.GONE
                }
            }
            "Admin" -> {
                toggleMode.visibility   = View.GONE
                buyerLayout.visibility  = View.GONE
                sellerLayout.visibility = View.GONE
                adminLayout.visibility  = View.VISIBLE
                findViewById<Button>(R.id.btnAdminUsers).setOnClickListener { go(AdminUsersActivity::class.java) }
                findViewById<Button>(R.id.btnAdminListings).setOnClickListener { go(AdminListingsActivity::class.java) }
            }
        }
    }

    // Reload avatar on every return — so it updates instantly after ProfileActivity
    override fun onResume() {
        super.onResume()
        if (userId != -1) ProfileManager.loadInto(this, userId, findViewById(R.id.btnProfileIcon))
    }

    private fun go(cls: Class<*>) {
        startActivity(Intent(this, cls).apply {
            putExtra("USER_ID", userId)
            putExtra("USERNAME", username)
            putExtra("ROLE", role)
        })
    }
}