package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class AddListingActivity : AppCompatActivity() {

    private var selectedImageUris = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_listing)

        val editMake: EditText = findViewById(R.id.editMake)
        val editModel: EditText = findViewById(R.id.editModel)
        val editYear: EditText = findViewById(R.id.editYear)
        val editPrice: EditText = findViewById(R.id.editPrice)
        val btnPick: Button = findViewById(R.id.btnPickImages)
        val txtCount: TextView = findViewById(R.id.txtImageCount)
        val btnSubmit: Button = findViewById(R.id.btnSubmitListing)

        val db = DatabaseHelper(this)

        // The Photo Picker Logic with Persistable Permissions
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
            if (uris.isNotEmpty()) {
                selectedImageUris.clear()
                uris.forEach { uri ->
                    try {
                        // This grants long-term access to the image file
                        contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    selectedImageUris.add(uri.toString())
                }
                txtCount.text = "${uris.size} images selected"
            }
        }

        btnPick.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        btnSubmit.setOnClickListener {
            val make = editMake.text.toString()
            val model = editModel.text.toString()
            val year = editYear.text.toString().toIntOrNull() ?: 0
            val price = editPrice.text.toString().toDoubleOrNull() ?: 0.0

            // FIX: Get the actual name passed from DashboardActivity
            val sellerName = intent.getStringExtra("USERNAME") ?: "Unknown Seller"

            if (make.isNotEmpty() && model.isNotEmpty()) {
                val result = db.addListing(sellerName, make, model, year, price, selectedImageUris)

                if (result != -1L) {
                    Toast.makeText(this, "Car Posted by $sellerName!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Database Error", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}