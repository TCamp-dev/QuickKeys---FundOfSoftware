package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class CarDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_car_details)

        val db           = DatabaseHelper(this)
        val carId        = intent.getIntExtra("CAR_ID", -1)
        val carInfo      = intent.getStringExtra("CAR_INFO") ?: "Car Details"
        val userId       = intent.getIntExtra("USER_ID", -1)
        val isSellerView = intent.getBooleanExtra("IS_SELLER_VIEW", false)

        findViewById<TextView>(R.id.txtDetailTitle).text = carInfo
        findViewById<Button>(R.id.btnBackToList).setOnClickListener { finish() }

        val car = db.getListingById(carId)
        if (car != null) {
            findViewById<ViewPager2>(R.id.viewPagerImages).adapter = ImageSliderAdapter(car.images)

            // Show extra details if available
            val txtLocation = findViewById<TextView?>(R.id.txtDetailLocation)
            val txtPhone    = findViewById<TextView?>(R.id.txtDetailPhone)
            val txtDesc     = findViewById<TextView?>(R.id.txtDetailDescription)
            txtLocation?.text = if (car.location.isNotEmpty()) "📍 ${car.location}" else ""
            txtPhone?.text    = if (car.phone.isNotEmpty()) "📞 ${car.phone}" else ""
            txtDesc?.text     = if (car.description.isNotEmpty()) car.description else ""
        }

        // Favorites button
        val btnFav = findViewById<Button?>(R.id.btnFavorite)
        if (btnFav != null && userId != -1 && !isSellerView) {
            updateFavBtn(btnFav, db.isFavorite(userId, carId))
            btnFav.setOnClickListener {
                if (db.isFavorite(userId, carId)) {
                    db.removeFavorite(userId, carId)
                    Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show()
                } else {
                    db.addFavorite(userId, carId)
                    Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
                }
                updateFavBtn(btnFav, db.isFavorite(userId, carId))
            }
        } else {
            btnFav?.visibility = View.GONE
        }

        val btnBuyNow = findViewById<Button>(R.id.btnBuyNow)
        if (isSellerView) {
            btnBuyNow.visibility = View.GONE
        } else {
            btnBuyNow.visibility = View.VISIBLE
            btnBuyNow.setOnClickListener {
                startActivity(Intent(this, PaymentActivity::class.java).apply {
                    putExtra("CAR_INFO", carInfo)
                    putExtra("CAR_ID", carId)
                    putExtra("USER_ID", userId)  // FIX: was "Buyer_ID" — inconsistent key caused null userId in PaymentActivity
                })
            }
        }
    }

    private fun updateFavBtn(btn: Button, isFav: Boolean) {
        btn.text = if (isFav) "♥  Saved" else "♡  Save"
    }
}