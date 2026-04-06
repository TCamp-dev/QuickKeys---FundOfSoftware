package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val carInfo = intent.getStringExtra("CAR_INFO") ?: "Unknown Car"

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

            // If everything passes (Happy Path)
            Toast.makeText(this, "Payment Successful! Car purchased.", Toast.LENGTH_LONG).show()

            // Return to the Dashboard after a successful purchase
            val intent = Intent(this, DashboardActivity::class.java)
            // Clear the activity stack so they can't press 'back' to return to payment
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}