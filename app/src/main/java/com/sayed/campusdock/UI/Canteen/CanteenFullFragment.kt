package com.sayed.campusdock.UI.Canteen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.sayed.campusdock.R

class CanteenFullFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.canteen_full_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ Use Safe Args to get the passed data
        val args = CanteenFullFragmentArgs.fromBundle(requireArguments())

        val name = args.canteenName
        val url = args.canteenUrl
        val isOpen = args.canteenOpen
        val id = args.canteenId    // This came a s string

        // Example: set name to a TextView
        view.findViewById<TextView>(R.id.cafeTitle)?.text = name
        view.findViewById<TextView>(R.id.cafeTiming)?.text = if (isOpen) "Open Now" else "Closed"

        Glide.with(requireContext())
            .load(url)
            .into(view.findViewById(R.id.cafeImage))
    }

}