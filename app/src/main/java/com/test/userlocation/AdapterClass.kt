package com.test.userlocation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.userlocation.NewBrd
import com.test.userlocation.R


class AdpaterClass(private val birds: List<NewBrd>) : RecyclerView.Adapter<AdpaterClass.BirdViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirdViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bird, parent, false)
        return BirdViewHolder(view)
    }

    override fun onBindViewHolder(holder: BirdViewHolder, position: Int) {
        val bird = birds[position]
        holder.bind(bird)
    }

    override fun getItemCount(): Int {
        return birds.size
    }
    class BirdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.birdName)
        private val textView2: TextView = itemView.findViewById(R.id.areaSeen)// Assuming you have a TextView in your item layout with the id "textView"

        fun bind(bird: NewBrd) {
            textView.text = "Bird Name: " + bird.birdName // Assuming "name" is a property of NewBrd class
            textView2.text = "Area Seen: " + bird.areaSeen
        }
    }

}