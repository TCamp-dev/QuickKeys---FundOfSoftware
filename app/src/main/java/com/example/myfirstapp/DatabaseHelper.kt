package com.example.myfirstapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Data models
data class CarListing(
    val id: Int,
    val sellerName: String,
    val make: String,
    val model: String,
    val year: Int,
    val price: Double,
    val images: List<String> = emptyList(),
    val purchased: Int       //added sold or not: 0 = no, 1 = yes
)

data class UserProfile(
    val id: Int,
    val username: String,
    val role: String
)

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "QuickKeys.db", null, 4) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT, role TEXT)")
        //added purchased field into table
        db.execSQL("CREATE TABLE listings (id INTEGER PRIMARY KEY AUTOINCREMENT, sellerName TEXT, make TEXT, model TEXT, year INTEGER, price REAL, purchased INTEGER DEFAULT 0)")
        db.execSQL("CREATE TABLE car_images (id INTEGER PRIMARY KEY AUTOINCREMENT, listingId INTEGER, imageUri TEXT, FOREIGN KEY(listingId) REFERENCES listings(id))")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS listings")
        db.execSQL("DROP TABLE IF EXISTS car_images")
        onCreate(db)
    }

    fun addUser(username: String, password: String, role: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("username", username)
            put("password", password)
            put("role", role)
        }
        return db.insert("users", null, values)
    }

    fun getUserRole(username: String, password: String): String? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT role FROM users WHERE username = ? AND password = ?", arrayOf(username, password))
        var role: String? = null
        if (cursor.moveToFirst()) {
            role = cursor.getString(0)
        }
        cursor.close()
        return role
    }

    fun addListing(seller: String, make: String, model: String, year: Int, price: Double, imageUris: List<String>): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("sellerName", seller)
            put("make", make)
            put("model", model)
            put("year", year)
            put("price", price)
            put("purchased", 0)
        }

        val listingId = db.insert("listings", null, values)

        if (listingId != -1L) {
            for (uri in imageUris) {
                val imgValues = ContentValues().apply {
                    put("listingId", listingId)
                    put("imageUri", uri)
                }
                db.insert("car_images", null, imgValues)
            }
        }
        return listingId
    }

    fun getAllListings(): List<CarListing> {
        val carList = mutableListOf<CarListing>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM listings WHERE purchased = 0", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val seller = cursor.getString(cursor.getColumnIndexOrThrow("sellerName"))
                val make = cursor.getString(cursor.getColumnIndexOrThrow("make"))
                val model = cursor.getString(cursor.getColumnIndexOrThrow("model"))
                val year = cursor.getInt(cursor.getColumnIndexOrThrow("year"))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"))
                val images = getImagesForListing(id)
                val purchased = cursor.getInt(cursor.getColumnIndexOrThrow("purchased")) //added
                carList.add(CarListing(id, seller, make, model, year, price, images, purchased))  //added purchased
            } while (cursor.moveToNext())
        }
        cursor.close()
        return carList
    }

    private fun getImagesForListing(listingId: Int): List<String> {
        val uris = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT imageUri FROM car_images WHERE listingId = ?", arrayOf(listingId.toString()))

        if (cursor.moveToFirst()) {
            do {
                uris.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return uris
    }

    fun getListingsBySeller(sellerName: String): List<CarListing> {
        val carList = mutableListOf<CarListing>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM listings WHERE sellerName = ?", arrayOf(sellerName))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val make = cursor.getString(cursor.getColumnIndexOrThrow("make"))
                val model = cursor.getString(cursor.getColumnIndexOrThrow("model"))
                val year = cursor.getInt(cursor.getColumnIndexOrThrow("year"))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"))
                val images = getImagesForListing(id)
                val purchased = cursor.getInt(cursor.getColumnIndexOrThrow("purchased"))  //added purchase
                carList.add(CarListing(id, sellerName, make, model, year, price, images, purchased)) //added purchase
            } while (cursor.moveToNext())
        }
        cursor.close()
        return carList
    }

    fun deleteListing(listingId: Int) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("purchased", 1)
        }
        db.update("listings",values,"id = ?", arrayOf(listingId.toString()))
//        db.delete("car_images", "listingId = ?", arrayOf(listingId.toString()))
//        db.delete("listings", "id = ?", arrayOf(listingId.toString()))

    }

    // --- NEW ADMIN FUNCTIONS ---

    fun getAllUsers(): List<UserProfile> {
        val userList = mutableListOf<UserProfile>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
                val role = cursor.getString(cursor.getColumnIndexOrThrow("role"))
                userList.add(UserProfile(id, username, role))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return userList
    }

    fun deleteUser(userId: Int) {
        val db = this.writableDatabase
        db.delete("users", "id = ?", arrayOf(userId.toString()))
    }
}