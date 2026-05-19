package com.example.myfirstapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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
    val location: String,
    val phone: String,
    val description: String = ""
)

data class UserProfile(
    val id: Int,
    val username: String,
    val role: String
)

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "QuickKeys.db", null, 6) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT, role TEXT)")
        db.execSQL("CREATE TABLE listings (id INTEGER PRIMARY KEY AUTOINCREMENT, sellerId INTEGER, sellerName TEXT, make TEXT, model TEXT, year INTEGER, price REAL, location TEXT, phone TEXT, purchased INTEGER DEFAULT 0, description TEXT DEFAULT '')")
        db.execSQL("CREATE TABLE car_images (id INTEGER PRIMARY KEY AUTOINCREMENT, listingId INTEGER, imageUri TEXT, FOREIGN KEY(listingId) REFERENCES listings(id))")
        db.execSQL("CREATE TABLE purchases (id INTEGER PRIMARY KEY AUTOINCREMENT, listingId INTEGER, buyerId INTEGER)")
        db.execSQL("CREATE TABLE favorites (id INTEGER PRIMARY KEY AUTOINCREMENT, userId INTEGER, listingId INTEGER, UNIQUE(userId, listingId))")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS listings")
        db.execSQL("DROP TABLE IF EXISTS car_images")
        db.execSQL("DROP TABLE IF EXISTS purchases")
        db.execSQL("DROP TABLE IF EXISTS favorites")
        onCreate(db)
    }

    // ── Users ─────────────────────────────────────────────────────────────────

    fun addUser(username: String, password: String, role: String): Long =
        writableDatabase.insert("users", null, ContentValues().apply {
            put("username", username); put("password", password); put("role", role)
        })

    fun getUser(username: String, password: String): UserProfile? =
        readableDatabase.rawQuery(
            "SELECT id, username, role FROM users WHERE username=? AND password=?",
            arrayOf(username, password)
        ).use { c -> if (c.moveToFirst()) c.toUserProfile() else null }

    fun getUserById(id: Int): UserProfile? =
        readableDatabase.rawQuery("SELECT id, username, role FROM users WHERE id=?", arrayOf(id.toString()))
            .use { c -> if (c.moveToFirst()) c.toUserProfile() else null }

    fun getAllUsers(): List<UserProfile> {
        val list = mutableListOf<UserProfile>()
        readableDatabase.rawQuery("SELECT * FROM users", null).use { c ->
            while (c.moveToNext()) list.add(c.toUserProfile())
        }
        return list
    }

    fun deleteUser(userId: Int) = writableDatabase.delete("users", "id=?", arrayOf(userId.toString()))

    // ── Listings ──────────────────────────────────────────────────────────────

    fun addListing(sellerId: Int, seller: String, make: String, model: String,
                   year: Int, price: Double, location: String, phone: String,
                   description: String, imageUris: List<String>): Long {
        val db = writableDatabase
        val id = db.insert("listings", null, ContentValues().apply {
            put("sellerId", sellerId); put("sellerName", seller)
            put("make", make); put("model", model); put("year", year)
            put("price", price); put("location", location); put("phone", phone)
            put("description", description); put("purchased", 0)
        })
        if (id != -1L) imageUris.forEach { uri ->
            db.insert("car_images", null, ContentValues().apply {
                put("listingId", id); put("imageUri", uri)
            })
        }
        return id
    }

    fun getAllListings(): List<CarListing> =
        query("SELECT * FROM listings WHERE purchased=0 ORDER BY id DESC")

    fun getListingById(id: Int): CarListing? =
        query("SELECT * FROM listings WHERE id=?", arrayOf(id.toString())).firstOrNull()

    // FIX: Parameter is now Int (sellerId), not String (sellerName)
    // Old code passed a username string into an INTEGER column — always returned 0 results
    fun getListingsBySeller(sellerId: Int): List<CarListing> =
        query("SELECT * FROM listings WHERE sellerId=? ORDER BY id DESC", arrayOf(sellerId.toString()))

    fun getBuyerPurchases(buyerId: Int): List<CarListing> = query("""
        SELECT listings.*, listings.id AS id FROM listings
        INNER JOIN purchases ON listings.id = purchases.listingId
        WHERE purchases.buyerId=? ORDER BY purchases.id DESC
    """.trimIndent(), arrayOf(buyerId.toString()))

    fun getSellerSoldListings(sellerId: Int): List<CarListing> = query("""
        SELECT listings.* FROM listings
        INNER JOIN purchases ON listings.id = purchases.listingId
        WHERE listings.sellerId=? ORDER BY purchases.id DESC
    """.trimIndent(), arrayOf(sellerId.toString()))

    fun searchAndSortListings(searchQuery: String, sortOption: String): List<CarListing> {
        var sql = "SELECT * FROM listings WHERE purchased=0"
        val args = mutableListOf<String>()
        if (searchQuery.isNotEmpty()) {
            sql += " AND (LOWER(make) LIKE LOWER(?) OR LOWER(model) LIKE LOWER(?) OR LOWER(location) LIKE LOWER(?))"
            val p = "%${searchQuery.lowercase()}%"
            args += listOf(p, p, p)
        }
        sql += when (sortOption) {
            "Price: Low to High" -> " ORDER BY price ASC"
            "Price: High to Low" -> " ORDER BY price DESC"
            "Year: Newest First" -> " ORDER BY year DESC"
            "Year: Oldest First" -> " ORDER BY year ASC"
            else -> " ORDER BY id DESC"
        }
        return query(sql, args.toTypedArray())
    }

    fun deleteListing(listingId: Int) {
        writableDatabase.also {
            it.delete("car_images", "listingId=?", arrayOf(listingId.toString()))
            it.delete("listings", "id=?", arrayOf(listingId.toString()))
            it.delete("favorites", "listingId=?", arrayOf(listingId.toString()))
        }
    }

    fun addPurchase(listingId: Int, buyerId: Int) {
        writableDatabase.also {
            it.update("listings", ContentValues().apply { put("purchased", 1) }, "id=?", arrayOf(listingId.toString()))
            it.insert("purchases", null, ContentValues().apply {
                put("listingId", listingId); put("buyerId", buyerId)
            })
        }
    }

    // ── Favorites ─────────────────────────────────────────────────────────────

    fun addFavorite(userId: Int, listingId: Int) {
        writableDatabase.insertWithOnConflict("favorites", null, ContentValues().apply {
            put("userId", userId); put("listingId", listingId)
        }, SQLiteDatabase.CONFLICT_IGNORE)
    }

    fun removeFavorite(userId: Int, listingId: Int) =
        writableDatabase.delete("favorites", "userId=? AND listingId=?", arrayOf(userId.toString(), listingId.toString()))

    fun isFavorite(userId: Int, listingId: Int): Boolean =
        readableDatabase.rawQuery(
            "SELECT id FROM favorites WHERE userId=? AND listingId=?",
            arrayOf(userId.toString(), listingId.toString())
        ).use { it.moveToFirst() }

    fun getFavorites(userId: Int): List<CarListing> = query("""
        SELECT listings.* FROM listings
        INNER JOIN favorites ON listings.id = favorites.listingId
        WHERE favorites.userId=? AND listings.purchased=0
    """.trimIndent(), arrayOf(userId.toString()))

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun getImages(listingId: Int): List<String> {
        val uris = mutableListOf<String>()
        readableDatabase.rawQuery("SELECT imageUri FROM car_images WHERE listingId=?", arrayOf(listingId.toString()))
            .use { c -> while (c.moveToNext()) uris.add(c.getString(0)) }
        return uris
    }

    private fun query(sql: String, args: Array<String> = emptyArray()): List<CarListing> {
        val list = mutableListOf<CarListing>()
        readableDatabase.rawQuery(sql, args).use { c ->
            while (c.moveToNext()) {
                val id = c.getInt(c.getColumnIndexOrThrow("id"))
                list.add(CarListing(
                    id = id,
                    sellerId = c.getInt(c.getColumnIndexOrThrow("sellerId")),
                    sellerName = c.getString(c.getColumnIndexOrThrow("sellerName")),
                    make = c.getString(c.getColumnIndexOrThrow("make")),
                    model = c.getString(c.getColumnIndexOrThrow("model")),
                    year = c.getInt(c.getColumnIndexOrThrow("year")),
                    price = c.getDouble(c.getColumnIndexOrThrow("price")),
                    images = getImages(id),
                    purchased = c.getInt(c.getColumnIndexOrThrow("purchased")),
                    location = c.getString(c.getColumnIndexOrThrow("location")),
                    phone = c.getString(c.getColumnIndexOrThrow("phone")),
                    description = c.getString(c.getColumnIndexOrThrow("description"))
                ))
            }
        }
        return list
    }

    private fun android.database.Cursor.toUserProfile() = UserProfile(
        getInt(getColumnIndexOrThrow("id")),
        getString(getColumnIndexOrThrow("username")),
        getString(getColumnIndexOrThrow("role"))
    )
}