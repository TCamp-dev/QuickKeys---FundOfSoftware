package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MyListingsActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var listView: ListView
    private var username: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_my_listings)

        db = DatabaseHelper(this)
        listView = findViewById(R.id.listViewMyCars)
        username = intent.getStringExtra("USERNAME") ?: "User"
        val userId = intent.getIntExtra("USER_ID", -1)

        loadMyListings(userId)

        listView.setOnItemClickListener { _, _, position, _ ->
            val carList = db.getListingsBySeller(userId)
            val selectedCar = carList[position]

            val intent = Intent(this, CarDetailsActivity::class.java)
            intent.putExtra("CAR_ID", selectedCar.id)
            val carInfoString = "${selectedCar.year} ${selectedCar.make} ${selectedCar.model} - $${selectedCar.price}\nSold by: ${selectedCar.sellerName}"
            intent.putExtra("CAR_INFO", carInfoString)

            // NEW: Tell the details screen that the seller is viewing this
            intent.putExtra("IS_SELLER_VIEW", true)

            startActivity(intent)
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            val carList = db.getListingsBySeller(userId)
            val selectedCar = carList[position]

            AlertDialog.Builder(this)
                .setTitle("Delete Listing")
                .setMessage("Are you sure you want to delete your ${selectedCar.year} ${selectedCar.make}?")
                .setPositiveButton("Delete") { _, _ ->
                    db.deleteListing(selectedCar.id)
                    Toast.makeText(this, "Listing deleted", Toast.LENGTH_SHORT).show()
                    loadMyListings(userId)
                }
                .setNegativeButton("Cancel", null)
                .show()

            true
        }
    }

    private fun loadMyListings(id: Int) {
        val carList = db.getListingsBySeller(id)
        val adapter = CarAdapter(this, carList)
        listView.adapter = adapter
    }
}