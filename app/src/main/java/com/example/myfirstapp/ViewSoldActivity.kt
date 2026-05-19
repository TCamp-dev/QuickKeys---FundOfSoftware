package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ViewSoldActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private var carList: List<CarListing> = emptyList()
    private var userId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_my_listings)

        db     = DatabaseHelper(this)
        userId = intent.getIntExtra("USER_ID", -1)

        findViewById<TextView>(R.id.txtMyListingsTitle).text = "My Sales History"
        findViewById<TextView>(R.id.txtTapInfo).text = "Cars you have sold"

        val listView = findViewById<ListView>(R.id.listViewMyCars)
        carList = db.getSellerSoldListings(userId)
        listView.adapter = CarAdapter(this, carList)

        listView.setOnItemClickListener { _, _, position, _ ->
            val car = carList[position]
            startActivity(Intent(this, CarDetailsActivity::class.java).apply {
                putExtra("CAR_ID", car.id)
                putExtra("USER_ID", userId)
                putExtra("CAR_INFO", "${car.year} ${car.make} ${car.model} - $${car.price}\nSold by: ${car.sellerName}")
                putExtra("IS_SELLER_VIEW", true)
            })
        }
    }
}