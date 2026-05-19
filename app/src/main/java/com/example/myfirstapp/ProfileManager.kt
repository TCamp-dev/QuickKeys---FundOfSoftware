package com.example.myfirstapp

import android.content.Context
import android.net.Uri
import android.widget.ImageView

/**
 * Singleton that saves/loads the profile picture URI using SharedPreferences.
 * This is why the avatar was disappearing — it was only stored in memory.
 * Now it survives navigation, back presses, and app restarts.
 */
object ProfileManager {

    private const val PREFS_NAME = "quickkeys_prefs"
    private const val KEY_PROFILE_URI = "profile_image_uri_"

    fun saveProfileImageUri(context: Context, userId: Int, uri: Uri) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString("$KEY_PROFILE_URI$userId", uri.toString())
            .apply()
    }

    fun getProfileImageUri(context: Context, userId: Int): Uri? {
        val uriString = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString("$KEY_PROFILE_URI$userId", null)
        return if (uriString != null) Uri.parse(uriString) else null
    }

    fun clearProfileImage(context: Context, userId: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove("$KEY_PROFILE_URI$userId")
            .apply()
    }

    /**
     * Loads profile image into any ImageView, with fallback to default icon.
     * Call this from Dashboard AND Profile so the avatar stays in sync.
     */
    fun loadInto(context: Context, userId: Int, imageView: ImageView) {
        val uri = getProfileImageUri(context, userId)
        if (uri != null) {
            imageView.setImageURI(null) // clear cache first
            imageView.setImageURI(uri)
        } else {
            imageView.setImageResource(android.R.drawable.ic_menu_myplaces)
        }
    }
}
