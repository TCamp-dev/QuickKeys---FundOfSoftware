package com.example.myfirstapp

import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class AdminListingsActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var listView: ListView
    private var carList: List<CarListing> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // We are reusing the layout you already made!
        supportActionBar?.hide()
        setContentView(R.layout.activity_my_listings)

        db = DatabaseHelper(this)
        listView = findViewById(R.id.listViewMyCars)

        // Change the title dynamically so it says Admin
        val titleText: TextView = findViewById(R.id.txtMyListingsTitle)
        titleText.text = "Admin: Global Listings Control"

        loadAllListings()

        if (carList.isEmpty()) {
            Toast.makeText(this, "No listings available", Toast.LENGTH_SHORT).show()
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->

            val selectedCar = carList[position]   // SAFE


            AlertDialog.Builder(this)
                .setTitle("Admin Override: Delete Listing")
                .setMessage("Delete ${selectedCar.year} ${selectedCar.make} posted by ${selectedCar.sellerName}?")
                .setPositiveButton("Delete") { _, _ ->
                    db.deleteListing(selectedCar.id)
                    Toast.makeText(this, "Listing removed from platform.", Toast.LENGTH_SHORT).show()
                    loadAllListings()
                }
                .setNegativeButton("Cancel", null)
                .show()

            true
        }
    }

    private fun loadAllListings() {
        carList = db.getAllListings()
        val adapter = CarAdapter(this, carList)
        listView.adapter = adapter
    }
}