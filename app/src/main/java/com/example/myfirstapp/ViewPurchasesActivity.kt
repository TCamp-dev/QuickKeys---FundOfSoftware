package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ViewPurchasesActivity : AppCompatActivity()  {
    private lateinit var db: DatabaseHelper
    private lateinit var listView: ListView
    private lateinit var carList: List<CarListing>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_my_listings)

        db = DatabaseHelper(this)
        val id = intent.getIntExtra("USER_ID", -1)
        val user = db.getUserById(id)
        var username = ""
        if(user != null)
        {
            username = user.username
        }

        val purchasesText: TextView = findViewById(R.id.txtMyListingsTitle)
        val tapInfo: TextView = findViewById(R.id.txtTapInfo)
        purchasesText.text = "My Purchases"
        tapInfo.text = "Click to view details"

        listView = findViewById(R.id.listViewMyCars)

        loadMyListings(id)

        listView.setOnItemClickListener { _, _, position, _ ->

            val selectedCar = carList[position]

            val intent = Intent(this, CarDetailsActivity::class.java)
            intent.putExtra("CAR_ID", selectedCar.id)

            val carInfoString =
                "${selectedCar.year} ${selectedCar.make} ${selectedCar.model} - $${selectedCar.price}\n" +
                        "Sold by: ${selectedCar.sellerName}\n" +
                        "Bought By: $username"

            intent.putExtra("CAR_INFO", carInfoString)
            intent.putExtra("IS_SELLER_VIEW", true)

            startActivity(intent)
        }

    }

    private fun loadMyListings(id: Int) {
        carList = db.getBuyerPurchases(id)
        val adapter = CarAdapter(this, carList)
        listView.adapter = adapter
    }
}
