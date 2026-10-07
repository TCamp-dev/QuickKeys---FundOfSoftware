package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class BrowseListingsActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var listView: ListView
    private var currentCarList: List<CarListing> = emptyList()
    private var userId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_browse_listings)

        db     = DatabaseHelper(this)
        userId = intent.getIntExtra("USER_ID", -1)

        listView = findViewById(R.id.listViewCars)
        val etSearch    = findViewById<EditText>(R.id.etSearch)
        val spinnerSort = findViewById<Spinner>(R.id.spinnerSort)
        val btnSearch   = findViewById<Button>(R.id.btnSearch)

        val sortOptions = arrayOf("Newest First", "Price: Low to High", "Price: High to Low", "Year: Newest First", "Year: Oldest First")
        spinnerSort.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sortOptions)

        refresh("", "Newest First")

        btnSearch.setOnClickListener {
            refresh(etSearch.text.toString().trim(), spinnerSort.selectedItem.toString())
        }

        spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>, v: View?, pos: Int, id: Long) {
                refresh(etSearch.text.toString().trim(), sortOptions[pos])
            }
            override fun onNothingSelected(p: AdapterView<*>) {}
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val car = currentCarList[position]
            startActivity(Intent(this, CarDetailsActivity::class.java).apply {
                putExtra("CAR_ID", car.id)
                putExtra("USER_ID", userId)
                putExtra("CAR_INFO", "${car.year} ${car.make} ${car.model} - $${car.price}\nSold by: ${car.sellerName}")
            })
        }
    }

    private fun refresh(query: String, sort: String) {
        currentCarList = db.searchAndSortListings(query, sort)
        listView.adapter = CarAdapter(this, currentCarList)
    }
}