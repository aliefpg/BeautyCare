package com.example.skindiagnosisai

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class OnboardingItem(
    val image: Int,
    val title: String,
    val description: String
)

class OnboardingAdapter(private val items: List<OnboardingItem>) :
    RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        return OnboardingViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.onboarding_item_container,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val image = view.findViewById<ImageView>(R.id.ivOnboardingImage)
        private val textTitle = view.findViewById<TextView>(R.id.tvOnboardingTitle)
        private val textDescription = view.findViewById<TextView>(R.id.tvOnboardingDescription)

        fun bind(item: OnboardingItem) {
            image.setImageResource(item.image)
            textTitle.text = item.title
            textDescription.text = item.description
        }
    }
}