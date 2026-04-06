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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // We are reusing the layout you already made!
        setContentView(R.layout.activity_my_listings)

        db = DatabaseHelper(this)
        listView = findViewById(R.id.listViewMyCars)

        // Change the title dynamically so it says Admin
        val titleText: TextView = findViewById(R.id.txtMyListingsTitle)
        titleText.text = "Admin: Global Listings Control"

        loadAllListings()

        listView.setOnItemLongClickListener { _, _, position, _ ->
            val carList = db.getAllListings()
            val selectedCar = carList[position]

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
        // Admins fetch ALL listings, not just ones by a specific seller
        val carList = db.getAllListings()
        val adapter = CarAdapter(this, carList)
        listView.adapter = adapter
    }
}