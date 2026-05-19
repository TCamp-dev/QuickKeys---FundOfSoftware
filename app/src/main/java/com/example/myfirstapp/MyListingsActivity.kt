package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MyListingsActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var listView: ListView
    private var userId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_my_listings)

        db     = DatabaseHelper(this)
        userId = intent.getIntExtra("USER_ID", -1)
        listView = findViewById(R.id.listViewMyCars)

        loadMyListings()

        listView.setOnItemClickListener { _, _, position, _ ->
            val car = db.getListingsBySeller(userId)[position]
            startActivity(Intent(this, CarDetailsActivity::class.java).apply {
                putExtra("CAR_ID", car.id)
                putExtra("USER_ID", userId)
                putExtra("CAR_INFO", "${car.year} ${car.make} ${car.model} - $${car.price}\nSold by: ${car.sellerName}")
                putExtra("IS_SELLER_VIEW", true)
            })
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            val car = db.getListingsBySeller(userId)[position]
            AlertDialog.Builder(this)
                .setTitle("Delete Listing")
                .setMessage("Delete your ${car.year} ${car.make} ${car.model}?")
                .setPositiveButton("Delete") { _, _ ->
                    db.deleteListing(car.id)
                    Toast.makeText(this, "Listing deleted", Toast.LENGTH_SHORT).show()
                    loadMyListings()
                }
                .setNegativeButton("Cancel", null).show()
            true
        }
    }

    private fun loadMyListings() {
        listView.adapter = CarAdapter(this, db.getListingsBySeller(userId))
    }
}