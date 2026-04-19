package com.example.myfirstapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri

class ProfileActivity : AppCompatActivity()
{
    //private var selectedImageUris = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_profile)

        val db = DatabaseHelper(this)
        val btnProfPick = findViewById<Button>(R.id.btnEditPhoto)
        val userId = intent.getIntExtra("USER_ID", -1)
        val user = db.getUserById(userId)
        val imgProfilePic = findViewById<ImageView>(R.id.imgProfilePic)

        val savedUri = user?.profileImage

        if (!savedUri.isNullOrEmpty()) {
            imgProfilePic.setImageURI(savedUri.toUri())
        }


        val userText: TextView = findViewById(R.id.txtProfileName)
        val roleText: TextView = findViewById(R.id.txtProfileRole)
//        val emailText: EditText = findViewById(R.id.btnProfileEmail)
//        val addressText: EditText = findViewById(R.id.btnProfileAddress)

//        val email = emailText.text.toString()
//        val address = addressText.text.toString()


        if (user != null) {
            userText.text = user.username
            roleText.text = user.role
        }

         val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

                if (uri != null) {

                    try {
                        contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    val uriString = uri.toString()
                    imgProfilePic.setImageURI(uri)

                    db.updateProfileImage(userId, uriString)
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

//        findViewById<Button>(R.id.btnSalesHistory).setOnClickListener {
//            val intent = Intent(this, ViewSoldActivity::class.java) //change to view sold
//            intent.putExtra("USER_ID", userId)
//            startActivity(intent)
//        }

        findViewById<Button>(R.id.btnDeleteAccount).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This cannot be undone.")
                .setPositiveButton("Delete") { _, _ ->

                    db.deleteUser(userId)

                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                    Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

    }

}