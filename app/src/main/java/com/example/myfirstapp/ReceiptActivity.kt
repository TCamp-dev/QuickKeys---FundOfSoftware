package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ReceiptActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_receipt)

        val userId = intent.getIntExtra("USER_ID", -1)

        findViewById<TextView>(R.id.txtOrderNumber).text   = "Order #${intent.getStringExtra("ORDER_NUMBER") ?: "------"}"
        findViewById<TextView>(R.id.txtReceiptCarInfo).text = intent.getStringExtra("CAR_INFO") ?: ""
        findViewById<TextView>(R.id.txtPickupLocation).text = intent.getStringExtra("LOCATION") ?: ""
        findViewById<TextView>(R.id.txtContactPhone).text   = intent.getStringExtra("PHONE") ?: ""

        findViewById<Button>(R.id.btnBackToDashboard).setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra("USER_ID", userId)
            })
            finish()
        }
    }
}