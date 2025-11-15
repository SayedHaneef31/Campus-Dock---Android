package com.sayed.campusdock.Adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sayed.campusdock.Data.Socials.PollOption
import com.sayed.campusdock.R

class PollAdapter(
    private var options: List<PollOption>,
    private val onOptionClicked: (PollOption) -> Unit
) : RecyclerView.Adapter<PollAdapter.PollViewHolder>() {

    class PollViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val optionText: TextView = view.findViewById(R.id.poll_option_text)
        val progressBar: View = view.findViewById(R.id.poll_progress_bar)
        val container: View = view.findViewById(R.id.cl_option_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PollViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_poll_option_item, parent, false)
        return PollViewHolder(view)
    }

    override fun onBindViewHolder(holder: PollViewHolder, position: Int) {
        val option = options[position]

        // Set the text with the option and percentage
        holder.optionText.text = "${option.text} "

        // This is the key part to make the progress bar dynamic
        // Use a ViewTreeObserver to get the container's width after the layout has been drawn
        holder.container.post {
            val containerWidth = holder.container.width
            if (containerWidth > 0) {
                // Calculate the desired width for the progress bar
                val newWidth = (containerWidth * option.percentage) / 100

                // Update the layout parameters to set the new width
                val params = holder.progressBar.layoutParams
                params.width = newWidth
                holder.progressBar.layoutParams = params
            }
        }

        // Add an onClickListener to the whole container to handle voting
        holder.container.setOnClickListener {
            onOptionClicked(option)
        }
    }

    override fun getItemCount() = options.size



    fun updateData(newOptions: List<PollOption>) {
        this.options = newOptions
        notifyDataSetChanged()
    }
}