package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_profile)

        val db     = DatabaseHelper(this)
        val userId = intent.getIntExtra("USER_ID", -1)
        val user   = db.getUserById(userId)
        val imgProfilePic = findViewById<ImageView>(R.id.imgProfilePic)

        // FIX: Load persisted image via ProfileManager instead of just holding URI in memory
        ProfileManager.loadInto(this, userId, imgProfilePic)

        if (user != null) {
            findViewById<TextView>(R.id.txtProfileName).text = user.username
            findViewById<TextView>(R.id.txtProfileRole).text = user.role
        }

        findViewById<TextView>(R.id.txtPurchaseCount).text = db.getBuyerPurchases(userId).size.toString()
        findViewById<TextView>(R.id.txtSalesCount).text    = db.getSellerSoldListings(userId).size.toString()

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                try {
                    contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } catch (e: Exception) { e.printStackTrace() }
                ProfileManager.saveProfileImageUri(this, userId, uri)
                imgProfilePic.setImageURI(null)
                imgProfilePic.setImageURI(uri)
            }
        }

        findViewById<Button>(R.id.btnEditPhoto).setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // FIX: These are LinearLayouts in XML, not Buttons — was crashing + redirecting to login
        findViewById<LinearLayout>(R.id.btnPurchaseHistory).setOnClickListener {
            startActivity(Intent(this, ViewPurchasesActivity::class.java).putExtra("USER_ID", userId))
        }
        findViewById<LinearLayout>(R.id.btnSalesHistory).setOnClickListener {
            startActivity(Intent(this, ViewSoldActivity::class.java).putExtra("USER_ID", userId))
        }
        findViewById<LinearLayout>(R.id.btnEditDetails).setOnClickListener {
            Toast.makeText(this, "Edit details coming soon", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnDeleteAccount).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Permanently delete your account and all listings?")
                .setPositiveButton("Delete") { _, _ ->
                    ProfileManager.clearProfileImage(this, userId)
                    db.deleteUser(userId)
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                    finish()
                }
                .setNegativeButton("Cancel", null).show()
        }
    }
}