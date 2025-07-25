package com.sayed.campusdock.UI.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sayed.campusdock.databinding.HomeFragmentBinding

class HomeFragment : Fragment() {

    //Enable view binding
    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        //This inflates your layout and prepares it for rendering.




        return binding.root

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // avoid memory leaks
    }
}