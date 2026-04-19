package com.example.myfirstapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class AddListingActivity : AppCompatActivity() {

    private var selectedImageUris = mutableListOf<String>()

    private fun saveImageToInternalStorage(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri)
        val fileName = "car_${System.currentTimeMillis()}.jpg"
        val file = File(filesDir, fileName)

        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)

        inputStream?.close()
        outputStream.close()

        return file.absolutePath
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_add_listing)

        val editMake: EditText = findViewById(R.id.editMake)
        val editModel: EditText = findViewById(R.id.editModel)
        val editYear: EditText = findViewById(R.id.editYear)
        val editPrice: EditText = findViewById(R.id.editPrice)
        val editLocation: EditText = findViewById(R.id.editLocation)
        val editPhone: EditText = findViewById(R.id.editPhone)
        val btnPick: Button = findViewById(R.id.btnPickImages)
        val txtCount: TextView = findViewById(R.id.txtImageCount)
        val btnSubmit: Button = findViewById(R.id.btnSubmitListing)

        val db = DatabaseHelper(this)

        val userId = intent.getIntExtra("USER_ID", -1)
        val user = db.getUserById(userId)
        var sellerName = ""
        if (user != null)
        {
            sellerName = user.username
        }


        // The Photo Picker Logic with Persistable Permissions
        val pickImages = registerForActivityResult(
            ActivityResultContracts.OpenMultipleDocuments()
        ) { uris ->
            if (uris.isNotEmpty()) {
                selectedImageUris.clear()
                uris.forEach { uri ->
//                    try {
//                        // This grants long-term access to the image file
//                        contentResolver.takePersistableUriPermission(
//                            uri,
//                            Intent.FLAG_GRANT_READ_URI_PERMISSION
//                        )
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
                    val savedPath = saveImageToInternalStorage(uri)
                    selectedImageUris.add(savedPath)

                }
                txtCount.text = "${uris.size} images selected"
            }
        }

        btnPick.setOnClickListener {
            pickImages.launch(arrayOf("image/*"))
        }

        btnSubmit.setOnClickListener {
            val make = editMake.text.toString().trim()
            val model = editModel.text.toString().trim()
            val year = editYear.text.toString().toIntOrNull() ?: 0
            val price = editPrice.text.toString().toDoubleOrNull() ?: 0.0
            val location = editLocation.text.toString().trim()
            val phone = editPhone.text.toString().trim()


            // FIX: Get the actual name passed from DashboardActivity



            if (make.isNotEmpty() && model.isNotEmpty()) {
                val result = db.addListing(userId, sellerName, make, model, year, price, location, phone, selectedImageUris)

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