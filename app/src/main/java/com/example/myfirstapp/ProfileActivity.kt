package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity()
{
    private var selectedImageUris = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_profile)

        val db = DatabaseHelper(this)
        var btnProfPick = findViewById<Button>(R.id.btnEditPhoto)
        val userId = intent.getIntExtra("USER_ID", -1)
        val user = db.getUserById(userId)
        val imgProfilePic = findViewById<ImageView>(R.id.imgProfilePic)


        val userText: TextView = findViewById(R.id.txtProfileName)
        val roleText: TextView = findViewById(R.id.txtProfileRole)
        val emailText: TextView = findViewById(R.id.txtProfileEmail)

        if (user != null) {
            userText.text = user.username
            roleText.text = user.role
        }

         val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

                if (uri != null) {

                    selectedImageUris.clear()

                    try {
                        contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    selectedImageUris.add(uri.toString())
                    imgProfilePic.setImageURI(uri)
                }
            }

        btnProfPick.setOnClickListener {
            pickMedia.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        findViewById<Button>(R.id.btnPurchaseHistory).setOnClickListener {
            val intent = Intent(this, ViewPurchasesActivity::class.java) //change to view purchases
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnSalesHistory).setOnClickListener {
            val intent = Intent(this, ViewSoldActivity::class.java) //change to view sold
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }


    }

}