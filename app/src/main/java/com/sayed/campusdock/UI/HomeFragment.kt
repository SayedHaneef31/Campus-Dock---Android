package com.sayed.campusdock.UI

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.sayed.campusdock.Data.Canteen
import com.sayed.campusdock.R
import com.sayed.campusdock.RetrofitClient
import com.sayed.campusdock.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    //Enable view binding
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        //This inflates your layout and prepares it for rendering.

        fetchCanteens()
        return binding.root

    }

    private fun fetchCanteens() {
        lifecycleScope.launch {
            try {
                val canteens = RetrofitClient.instance.getCanteens()
                displayCanteens(canteens)
            } catch (e: Exception){
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("HomeFragment", "Error: ${e.message}")
            }

        }
    }

    private fun displayCanteens(canteens: List<Canteen>) {
        val container = binding.canteenContainer
        container.removeAllViews()

        for (canteen in canteens) {
            val card = layoutInflater.inflate(R.layout.canteen_card, container, false)

            val nameView = card.findViewById<TextView>(R.id.canteenName)
            val statusView = card.findViewById<TextView>(R.id.canteenStatus)

            nameView.text = canteen.name
            statusView.text = if (canteen.open) "Open" else "Closed"
            statusView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (canteen.open) R.color.green else R.color.red
                )
            )

            // Set click to navigate
            card.setOnClickListener {
                navigateToMenu(canteen.name)
            }

            container.addView(card)
        }
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