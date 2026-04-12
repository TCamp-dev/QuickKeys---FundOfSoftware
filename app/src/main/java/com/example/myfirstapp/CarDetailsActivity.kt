package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class CarDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_car_details)

        val carId = intent.getIntExtra("CAR_ID", -1)
        val carInfo = intent.getStringExtra("CAR_INFO") ?: "Car Details"

        // Check if the user viewing is the seller
        val isSellerView = intent.getBooleanExtra("IS_SELLER_VIEW", false)

        // FIXED: Matched these IDs to your XML file exactly
        val title: TextView = findViewById(R.id.txtDetailTitle)
        val viewPager: ViewPager2 = findViewById(R.id.viewPagerImages)
        val btnBuyNow: Button = findViewById(R.id.btnBuyNow)

        // NEW: Wired up the Back button you added to your XML
        val btnBackToList: Button = findViewById(R.id.btnBackToList)
        btnBackToList.setOnClickListener {
            finish() // This safely closes the details screen and returns to the list
        }

        title.text = carInfo

        val db = DatabaseHelper(this)
        val selectedCar = db.getAllListings().find { it.id == carId }

        if (selectedCar != null) {
            val adapter = ImageSliderAdapter(selectedCar.images)
            viewPager.adapter = adapter
        }

        // Logic to hide or use the Buy Button
        if (isSellerView) {
            // Hide the button if it's the seller's own car
            btnBuyNow.visibility = View.GONE
        } else {
            // Ensure it is visible for buyers and link it to the Payment Module
            btnBuyNow.visibility = View.VISIBLE
            btnBuyNow.setOnClickListener {
                val paymentIntent = Intent(this, PaymentActivity::class.java)
                paymentIntent.putExtra("CAR_INFO", carInfo)
                paymentIntent.putExtra("CAR_ID", carId)
                startActivity(paymentIntent)
            }
        }
    }
}