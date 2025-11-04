package com.sayed.campusdock.Adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sayed.campusdock.Data.Developer
import com.sayed.campusdock.R

class DeveloperAdapter(
    private val developers: List<Developer>
) : RecyclerView.Adapter<DeveloperAdapter.DeveloperViewHolder>() {

    // Make it appear infinite by returning a very large count
    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeveloperViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_developer_card, parent, false)
        return DeveloperViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeveloperViewHolder, position: Int) {
        // Use modulo to cycle through the actual developer list
        val actualPosition = position % developers.size
        val developer = developers[actualPosition]
        holder.bind(developer)
    }

    inner class DeveloperViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val photo: ImageView = itemView.findViewById(R.id.developerPhoto)
        private val name: TextView = itemView.findViewById(R.id.developerName)
        private val designation: TextView = itemView.findViewById(R.id.developerDesignation)
        private val email: TextView = itemView.findViewById(R.id.developerEmail)
        private val social: TextView = itemView.findViewById(R.id.developerSocial)
        private val emailLayout: LinearLayout = itemView.findViewById(R.id.emailLayout)
        private val linkedinLayout: LinearLayout = itemView.findViewById(R.id.linkedinLayout)

        fun bind(developer: Developer) {
            photo.setImageResource(developer.photoResId)
            name.text = developer.name
            designation.text = developer.designation
            email.text = "Email"
            social.text = "LinkedIn"

            // Email click listener
            emailLayout.setOnClickListener {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:${developer.email}")
                    putExtra(Intent.EXTRA_SUBJECT, "Hello from Campus Dock")
                }
                try {
                    itemView.context.startActivity(Intent.createChooser(intent, "Send Email"))
                } catch (e: Exception) {
                    // Handle error silently or show toast
                }
            }

            // LinkedIn click listener
            linkedinLayout.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(developer.social))
                try {
                    itemView.context.startActivity(intent)
                } catch (e: Exception) {
                    // Handle error silently or show toast
                }
            }
        }
    }
}
