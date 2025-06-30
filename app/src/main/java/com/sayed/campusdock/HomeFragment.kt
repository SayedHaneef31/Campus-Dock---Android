package com.sayed.campusdock

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sayed.campusdock.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.root.findViewById<View>(R.id.canteenCard1).setOnClickListener {
            navigateToMenu("Tea Man's Cafe")
        }
        binding.root.findViewById<View>(R.id.canteenCard2).setOnClickListener {
            navigateToMenu("Student Union Eats")
        }
        binding.root.findViewById<View>(R.id.canteenCard3).setOnClickListener {
            navigateToMenu("Library Bistro")
        }

        return binding.root

    }
    private fun navigateToMenu(canteenName: String) {
        val intent = Intent(requireContext(), CanteenMenuActivity::class.java)
        intent.putExtra("canteen_name", canteenName)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // avoid memory leaks
    }
}