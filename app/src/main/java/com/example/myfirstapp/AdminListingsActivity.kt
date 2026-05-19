package com.example.myfirstapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class AdminListingsActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_my_listings)

        db = DatabaseHelper(this)
        listView = findViewById(R.id.listViewMyCars)
        findViewById<TextView>(R.id.txtMyListingsTitle).text = "All Listings"
        findViewById<TextView>(R.id.txtTapInfo).text = "Hold a listing to delete it"

        load()

        listView.setOnItemLongClickListener { _, _, position, _ ->
            val car = db.getAllListings()[position]
            AlertDialog.Builder(this)
                .setTitle("Delete Listing")
                .setMessage("Delete ${car.year} ${car.make} ${car.model} by ${car.sellerName}?")
                .setPositiveButton("Delete") { _, _ ->
                    db.deleteListing(car.id)
                    Toast.makeText(this, "Listing removed", Toast.LENGTH_SHORT).show()
                    load()
                }
                .setNegativeButton("Cancel", null).show()
            true
        }
    }

    private fun load() {
        listView.adapter = CarAdapter(this, db.getAllListings())
    }
}