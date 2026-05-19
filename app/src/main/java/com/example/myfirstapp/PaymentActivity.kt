package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class PaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_payment)

        val db      = DatabaseHelper(this)
        val carInfo = intent.getStringExtra("CAR_INFO") ?: "Unknown Car"
        val carId   = intent.getIntExtra("CAR_ID", -1)
        val userId  = intent.getIntExtra("USER_ID", -1)  // FIX: was "Buyer_ID" in old CarDetailsActivity

        findViewById<TextView>(R.id.txtCheckoutSummary).text = carInfo

        val editCard   = findViewById<EditText>(R.id.editCardNumber)
        val editExpiry = findViewById<EditText>(R.id.editExpiry)
        val editCvv    = findViewById<EditText>(R.id.editCvv)

        findViewById<Button>(R.id.btnConfirmPayment).setOnClickListener {
            val card   = editCard.text.toString().trim()
            val expiry = editExpiry.text.toString().trim()
            val cvv    = editCvv.text.toString().trim()

            when {
                card.isEmpty() || expiry.isEmpty() || cvv.isEmpty() ->
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                card.length < 16 ->
                    Toast.makeText(this, "Invalid card number", Toast.LENGTH_SHORT).show()
                cvv.length < 3 ->
                    Toast.makeText(this, "Invalid CVV", Toast.LENGTH_SHORT).show()
                else -> {
                    val car      = db.getListingById(carId)
                    val location = car?.location ?: "Location TBD"
                    val phone    = car?.phone ?: "Phone TBD"
                    db.addPurchase(carId, userId)
                    val orderNumber = "QK-${Random.nextInt(100000, 999999)}"
                    startActivity(Intent(this, ReceiptActivity::class.java).apply {
                        putExtra("ORDER_NUMBER", orderNumber)
                        putExtra("CAR_INFO", carInfo)
                        putExtra("LOCATION", location)
                        putExtra("PHONE", phone)
                        putExtra("USER_ID", userId)
                    })
                    finish()
                }
            }
        }
    }
}