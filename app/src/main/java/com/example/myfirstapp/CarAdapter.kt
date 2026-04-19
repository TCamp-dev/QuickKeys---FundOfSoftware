package com.example.myfirstapp

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import java.io.File

class CarAdapter(context: Context, private val cars: List<CarListing>) :
    ArrayAdapter<CarListing>(context, R.layout.list_item_car, cars) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_car, parent, false)

        val currentCar = cars[position]

        val nameText = view.findViewById<TextView>(R.id.txtCarName)
        val priceText = view.findViewById<TextView>(R.id.txtCarPrice)
        val imageView = view.findViewById<ImageView>(R.id.imgCarThumbnail)

        nameText.text = "${currentCar.year} ${currentCar.make} ${currentCar.model}"
        priceText.text = "$${currentCar.price}"

        // Load the first image if it exists
        if (!currentCar.images.isNullOrEmpty()) {
            val file = File(currentCar.images[0])
            if (file.exists()) {
                imageView.setImageURI(Uri.fromFile(file))
            } else {
                imageView.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        } else {
            imageView.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        return view
    }
}