package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ReceiptActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_receipt)

        val txtOrderNum: TextView = findViewById(R.id.txtOrderNumber)
        val txtCarInfo: TextView = findViewById(R.id.txtReceiptCarInfo)
        val txtLocation: TextView = findViewById(R.id.txtPickupLocation)
        val txtPhone: TextView = findViewById(R.id.txtContactPhone)
        val btnDone: Button = findViewById(R.id.btnBackToDashboard)
        val buyerId = intent.getIntExtra("USER_ID", -1)

        // Retrieve the data passed from PaymentActivity
        txtOrderNum.text = "Order #" + (intent.getStringExtra("ORDER_NUMBER") ?: "000000")
        txtCarInfo.text = intent.getStringExtra("CAR_INFO")
        txtLocation.text = intent.getStringExtra("LOCATION")
        txtPhone.text = intent.getStringExtra("PHONE")

        btnDone.setOnClickListener {
            // Return to the Dashboard and clear history
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("USER_ID", buyerId)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}