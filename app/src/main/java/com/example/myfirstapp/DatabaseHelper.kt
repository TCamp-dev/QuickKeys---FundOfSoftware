package com.example.myfirstapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

// Data models
data class CarListing(
    val id: Int,
    val sellerId: Int,
    val sellerName: String,
    val make: String,
    val model: String,
    val year: Int,
    val price: Double,
    val images: List<String> = emptyList(),
    val purchased: Int,
    val location: String, // NEW
    val phone: String     // NEW

)



data class UserProfile(
    val id: Int,
    val username: String,
    val role: String
)

//data class Purchases (
//    val id: Int,
//    val listingId: Int,
//    val buyerId: Int,
//)


class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "QuickKeys.db", null, 5) {

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("DB", "CREATING TABLE NOW")
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT, role TEXT)")
        //added purchased field into table
        db.execSQL("CREATE TABLE listings (id INTEGER PRIMARY KEY AUTOINCREMENT, sellerId INTEGER, sellerName TEXT, make TEXT, model TEXT, year INTEGER, price REAL, location TEXT, phone TEXT, purchased INTEGER DEFAULT 0)")
        db.execSQL("CREATE TABLE car_images (id INTEGER PRIMARY KEY AUTOINCREMENT, listingId INTEGER, imageUri TEXT, FOREIGN KEY(listingId) REFERENCES listings(id))")
        Log.d("DB", "CREATING PURCHASES TABLE NOW")
        db.execSQL("CREATE TABLE purchases (id INTEGER PRIMARY KEY AUTOINCREMENT, listingId INTEGER, buyerId INTEGER)")

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS listings")
        db.execSQL("DROP TABLE IF EXISTS car_images")
        db.execSQL("DROP TABLE IF EXISTS purchases")

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

    // probably change this
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

    fun getUser(username: String, password: String): UserProfile? {
        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT id, username, role FROM users WHERE username = ? AND password = ?",
            arrayOf(username, password)
        )

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val user = cursor.getString(cursor.getColumnIndexOrThrow("username"))
            val role = cursor.getString(cursor.getColumnIndexOrThrow("role"))

            cursor.close()
            return UserProfile(id, user, role)
        }

        cursor.close()
        return null
    }

    fun getUserById(userId: Int): UserProfile? {
        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT id, username, role FROM users WHERE id = ?",
            arrayOf(userId.toString())
        )

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
            val role = cursor.getString(cursor.getColumnIndexOrThrow("role"))

            cursor.close()
            return UserProfile(id, username, role)
        }

        cursor.close()
        return null
    }

    fun addListing(sellerId: Int, seller: String, make: String, model: String, year: Int, price: Double, location: String, phone: String, imageUris: List<String>): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("sellerId", sellerId)
            put("sellerName", seller)
            put("make", make)
            put("model", model)
            put("year", year)
            put("price", price)
            put("location", location)
            put("phone", phone)
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
                val sellerId = cursor.getInt(cursor.getColumnIndexOrThrow("sellerId"))
                val seller = cursor.getString(cursor.getColumnIndexOrThrow("sellerName"))
                val make = cursor.getString(cursor.getColumnIndexOrThrow("make"))
                val model = cursor.getString(cursor.getColumnIndexOrThrow("model"))
                val year = cursor.getInt(cursor.getColumnIndexOrThrow("year"))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"))
                val images = getImagesForListing(id)
                val location = cursor.getString(cursor.getColumnIndexOrThrow("location"))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"))
                val purchased = cursor.getInt(cursor.getColumnIndexOrThrow("purchased")) //added
                carList.add(CarListing(id, sellerId,seller, make, model, year, price, images, purchased, location, phone))  //added purchased
            } while (cursor.moveToNext())
        }
        cursor.close()
        return carList
    }

    fun getListingById(id: Int): CarListing? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM listings WHERE id = ?", arrayOf(id.toString()))
        var car: CarListing? = null
        if (cursor.moveToFirst()) {
            val seller = cursor.getString(cursor.getColumnIndexOrThrow("sellerName"))
            val sellerId = cursor.getInt(cursor.getColumnIndexOrThrow("sellerId"))
            val make = cursor.getString(cursor.getColumnIndexOrThrow("make"))
            val model = cursor.getString(cursor.getColumnIndexOrThrow("model"))
            val year = cursor.getInt(cursor.getColumnIndexOrThrow("year"))
            val price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"))
            val location = cursor.getString(cursor.getColumnIndexOrThrow("location"))
            val phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"))
            val purchased = cursor.getInt(cursor.getColumnIndexOrThrow("purchased"))
            car = CarListing(
                id,
                sellerId,
                seller,
                make,
                model,
                year,
                price,
                emptyList(),
                purchased,
                location,
                phone
            )
        }
        cursor.close()
        return car
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
        val cursor = db.rawQuery("SELECT * FROM listings WHERE sellerId = ?", arrayOf(sellerName))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val sellerId = cursor.getInt(cursor.getColumnIndexOrThrow("sellerId"))
                val make = cursor.getString(cursor.getColumnIndexOrThrow("make"))
                val model = cursor.getString(cursor.getColumnIndexOrThrow("model"))
                val year = cursor.getInt(cursor.getColumnIndexOrThrow("year"))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"))
                val images = getImagesForListing(id)
                val purchased = cursor.getInt(cursor.getColumnIndexOrThrow("purchased"))  //added purchase
                val location = cursor.getString(cursor.getColumnIndexOrThrow("location"))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"))
                carList.add(CarListing(id, sellerId, sellerName, make, model, year, price, images, purchased, location, phone))//added purchase
            } while (cursor.moveToNext())
        }
        cursor.close()
        return carList
    }

    fun getSellerPurchased(userId: Int): List<CarListing> {
        val carList = mutableListOf<CarListing>()
        val db = this.readableDatabase

        val cursor = db.rawQuery(
            """
        SELECT listings.*
        FROM listings
        INNER JOIN purchases
        ON listings.id = purchases.listingId
        WHERE listings.sellerId = ?
        """.trimIndent(),
            arrayOf(userId.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val make = cursor.getString(cursor.getColumnIndexOrThrow("make"))
                val sellerName = cursor.getString(cursor.getColumnIndexOrThrow("sellerName"))
                val sellerId = cursor.getInt(cursor.getColumnIndexOrThrow("sellerId"))
                val model = cursor.getString(cursor.getColumnIndexOrThrow("model"))
                val year = cursor.getInt(cursor.getColumnIndexOrThrow("year"))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"))
                val images = getImagesForListing(id)
                val purchased = cursor.getInt(cursor.getColumnIndexOrThrow("purchased"))  //added purchase
                val location = cursor.getString(cursor.getColumnIndexOrThrow("location"))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"))
                carList.add(CarListing(id, sellerId, sellerName, make, model, year, price, images, purchased, location, phone))//added purchase
            } while (cursor.moveToNext())
        }
        cursor.close()
        return carList
    }

    fun deleteListing(listingId: Int) {
        val db = this.writableDatabase
        db.delete(
            "listings",
            "id = ?",
            arrayOf(listingId.toString())
        )
        db.close()
    }

    fun addPurchase(listingId: Int, buyerId: Int) {
        val db = this.writableDatabase

        // 1. Mark listing as purchased/sold
        val listingValues = ContentValues().apply {
            put("purchased", 1)
        }

        db.update(
            "listings",
            listingValues,
            "id = ?",
            arrayOf(listingId.toString())
        )

        // 2. Record purchase history
        val purchaseValues = ContentValues().apply {
            put("listingId", listingId)
            put("buyerId", buyerId)
        }

        db.insert("purchases", null, purchaseValues)
    }



    fun getBuyerPurchases(buyerId: Int): List<CarListing> {
        val carList = mutableListOf<CarListing>()
        val db = this.readableDatabase

        val cursor = db.rawQuery(
            """
        SELECT listings.id AS listingId,
               listings.sellerId,
               listings.sellerName,
               listings.make,
               listings.model,
               listings.year,
               listings.price,
               listings.location,
               listings.phone,
               listings.purchased
        FROM listings
        INNER JOIN purchases
        ON listings.id = purchases.listingId
        WHERE purchases.buyerId = ?
        """.trimIndent(),
            arrayOf(buyerId.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("listingId"))
                val sellerId = cursor.getInt(cursor.getColumnIndexOrThrow("sellerId"))
                val seller = cursor.getString(cursor.getColumnIndexOrThrow("sellerName"))
                val make = cursor.getString(cursor.getColumnIndexOrThrow("make"))
                val model = cursor.getString(cursor.getColumnIndexOrThrow("model"))
                val year = cursor.getInt(cursor.getColumnIndexOrThrow("year"))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"))
                val images = getImagesForListing(id)
                val location = cursor.getString(cursor.getColumnIndexOrThrow("location"))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"))
                val purchased = cursor.getInt(cursor.getColumnIndexOrThrow("purchased"))

                carList.add(
                    CarListing(
                        id,
                        sellerId,
                        seller,
                        make,
                        model,
                        year,
                        price,
                        images,
                        purchased,
                        location,
                        phone
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return carList
    }

    fun searchAndSortListings(searchQuery: String, sortOption: String): List<CarListing> {
        val carList = mutableListOf<CarListing>()
        val db = this.readableDatabase
        var sqlString = "SELECT * FROM listings WHERE purchased = 0"
        var selectionArgs = emptyArray<String>()

        if (searchQuery.isNotEmpty()) {
            sqlString += " AND (LOWER(make) LIKE LOWER(?) OR LOWER(model) LIKE LOWER(?))"
            val searchPattern = "%${searchQuery.lowercase()}%"
            selectionArgs = arrayOf(searchPattern, searchPattern)
        }

        sqlString += when (sortOption) {
            "Price: Low to High" -> " ORDER BY price ASC"
            "Price: High to Low" -> " ORDER BY price DESC"
            "Year: Newest First" -> " ORDER BY year DESC"
            "Year: Oldest First" -> " ORDER BY year ASC"
            else -> " ORDER BY id DESC"
        }

        val cursor = db.rawQuery(sqlString, selectionArgs)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val sellerId = cursor.getInt(cursor.getColumnIndexOrThrow("sellerId"))
                val seller = cursor.getString(cursor.getColumnIndexOrThrow("sellerName"))
                val make = cursor.getString(cursor.getColumnIndexOrThrow("make"))
                val model = cursor.getString(cursor.getColumnIndexOrThrow("model"))
                val year = cursor.getInt(cursor.getColumnIndexOrThrow("year"))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"))
                val location = cursor.getString(cursor.getColumnIndexOrThrow("location"))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"))
                val images = getImagesForListing(id)
                val purchased = cursor.getInt(cursor.getColumnIndexOrThrow("purchased"))

                carList.add(CarListing(id,sellerId, seller, make, model, year, price, images, purchased, location, phone))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return carList
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