package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class AddListingActivity : AppCompatActivity() {

    private val selectedImageUris = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_add_listing)

        val db     = DatabaseHelper(this)
        val userId = intent.getIntExtra("USER_ID", -1)
        val user   = db.getUserById(userId)
        val sellerName = user?.username ?: ""

        val editMake   = findViewById<EditText>(R.id.editMake)
        val editModel  = findViewById<EditText>(R.id.editModel)
        val editYear   = findViewById<EditText>(R.id.editYear)
        val editPrice  = findViewById<EditText>(R.id.editPrice)
        val editLoc    = findViewById<EditText>(R.id.editLocation)
        val editPhone  = findViewById<EditText>(R.id.editPhone)
        val editDesc   = findViewById<EditText?>(R.id.editDescription)
        val txtCount   = findViewById<TextView>(R.id.txtImageCount)

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
            if (uris.isNotEmpty()) {
                selectedImageUris.clear()
                uris.forEach { uri ->
                    try { contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
                    catch (e: Exception) { e.printStackTrace() }
                    selectedImageUris.add(uri.toString())
                }
                txtCount.text = "${uris.size} photo${if (uris.size != 1) "s" else ""} selected"
            }
        }

        findViewById<Button>(R.id.btnPickImages).setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        findViewById<Button>(R.id.btnSubmitListing).setOnClickListener {
            val make  = editMake.text.toString().trim()
            val model = editModel.text.toString().trim()
            val year  = editYear.text.toString().toIntOrNull() ?: 0
            val price = editPrice.text.toString().toDoubleOrNull() ?: 0.0
            val loc   = editLoc.text.toString().trim()
            val phone = editPhone.text.toString().trim()
            val desc  = editDesc?.text.toString().trim() ?: ""

            when {
                make.isEmpty() || model.isEmpty() -> Toast.makeText(this, "Make and model are required", Toast.LENGTH_SHORT).show()
                year < 1900 || year > 2100       -> Toast.makeText(this, "Please enter a valid year", Toast.LENGTH_SHORT).show()
                price <= 0                        -> Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show()
                else -> {
                    val result = db.addListing(userId, sellerName, make, model, year, price, loc, phone, desc, selectedImageUris)
                    if (result != -1L) {
                        Toast.makeText(this, "Listing posted!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}