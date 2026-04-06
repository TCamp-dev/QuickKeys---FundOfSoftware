package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class BrowseListingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_listings)

        // 1. Link the ListView from your layout
        // NOTE: Ensure your activity_browse_listings.xml uses the ID "listViewCars"
        val listView: ListView = findViewById(R.id.listViewCars)
        val db = DatabaseHelper(this)

        // 2. Fetch the data (Now returns a List of CarListing objects)
        val carList = db.getAllListings()

        // 3. Use the CUSTOM Adapter instead of the simple ArrayAdapter
        // This allows us to show the car's image, name, and price in one row
        val adapter = CarAdapter(this, carList)
        listView.adapter = adapter

        // 4. Handle clicking on a car
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedCar = carList[position]

            val intent = Intent(this, CarDetailsActivity::class.java)

            // We pass the unique ID so the Details page can fetch all images
            intent.putExtra("CAR_ID", selectedCar.id)

            // We still pass the text info for the title
            val carInfoString = "${selectedCar.year} ${selectedCar.make} ${selectedCar.model} - $${selectedCar.price}\nSold by: ${selectedCar.sellerName}"
            intent.putExtra("CAR_INFO", carInfoString)

            startActivity(intent)
        }
    }
}