package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.widget.Spinner

class BrowseListingsActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var spinnerSort: Spinner
    private lateinit var listView: ListView
    private lateinit var btnSearch: Button
    private lateinit var db: DatabaseHelper


    // We must track the current filtered list so the click listener knows exactly which car we clicked!
    private var currentCarList: List<CarListing> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_browse_listings)

        db = DatabaseHelper(this)

        // 1. Link the ListView from your layout
        // NOTE: Ensure your activity_browse_listings.xml uses the ID "listViewCars"
        listView = findViewById(R.id.listViewCars)
        etSearch = findViewById(R.id.etSearch)
        spinnerSort = findViewById(R.id.spinnerSort)
        btnSearch = findViewById(R.id.btnSearch)



        val sortOptions = arrayOf(
            "Newest First",
            "Price: Low to High",
            "Price: High to Low",
            "Year: Newest First",
            "Year: Oldest First"
        )
        // This takes our text array and turns it into actual dropdown items
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sortOptions)
        spinnerSort.adapter = spinnerAdapter

        updateListView("", "Newest First")

        // 4. Trigger Search when Button is clicked
        btnSearch.setOnClickListener {
            val searchQuery = etSearch.text.toString().trim()
            val sortOption = spinnerSort.selectedItem.toString()
            updateListView(searchQuery, sortOption)
        }

        spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val searchQuery = etSearch.text.toString().trim()
                val sortOption = sortOptions[position]
                updateListView(searchQuery, sortOption)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Not needed for this setup
            }
        }


        // 4. Handle clicking on a car
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedCar = currentCarList[position]

            val intent = Intent(this, CarDetailsActivity::class.java)

            // We pass the unique ID so the Details page can fetch all images
            intent.putExtra("CAR_ID", selectedCar.id)

            // We still pass the text info for the title
            val carInfoString = "${selectedCar.year} ${selectedCar.make} ${selectedCar.model} - $${selectedCar.price}\nSold by: ${selectedCar.sellerName}"
            intent.putExtra("CAR_INFO", carInfoString)

            startActivity(intent)
        }
    }

    // This talks to the DatabaseHelper and refreshes the ListView
    private fun updateListView(searchQuery: String, sortOption: String) {
        currentCarList = db.searchAndSortListings(searchQuery, sortOption) //in db helper
        val adapter = CarAdapter(this, currentCarList)
        listView.adapter = adapter
    }
}