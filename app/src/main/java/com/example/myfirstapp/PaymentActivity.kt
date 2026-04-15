package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random


class PaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_payment)

        val db = DatabaseHelper(this)

        val carInfo = intent.getStringExtra("CAR_INFO") ?: "Unknown Car"
        val carId = intent.getIntExtra("CAR_ID", -1)
        val userId = intent.getIntExtra("USER_ID", -1)
        val user = db.getUserById(userId)


        val summaryText: TextView = findViewById(R.id.txtCheckoutSummary)
        val editCard: EditText = findViewById(R.id.editCardNumber)
        val editExpiry: EditText = findViewById(R.id.editExpiry)
        val editCvv: EditText = findViewById(R.id.editCvv)
        val btnConfirm: Button = findViewById(R.id.btnConfirmPayment)

        // Display what they are buying
        summaryText.text = carInfo

        btnConfirm.setOnClickListener {
            val card = editCard.text.toString()
            val expiry = editExpiry.text.toString()
            val cvv = editCvv.text.toString()

            // Test Plan Validation (Happy & Sad Paths)
            if (card.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (card.length < 16) {
                Toast.makeText(this, "Invalid Card Number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (cvv.length < 3) {
                Toast.makeText(this, "Invalid CVV", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // NEW: Fetch location and phone BEFORE we delete the listing!
            val purchasedCar = db.getListingById(carId)
            val location = purchasedCar?.location ?: "Location TBD"
            val phone = purchasedCar?.phone ?: "Phone TBD"

            // Delete the listing
            db.addPurchase(carId, userId)

            // Generate Random Order Number
            val orderNumber = "QK-${Random.nextInt(100000, 999999)}"

            Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show()

            // Pass everything to the new Receipt Activity
            val intent = Intent(this, ReceiptActivity::class.java).apply {
                putExtra("ORDER_NUMBER", orderNumber)
                putExtra("CAR_INFO", carInfo)
                putExtra("LOCATION", location)
                putExtra("PHONE", phone)
            }
            startActivity(intent)
            finish()
        }
    }
}