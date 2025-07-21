package com.sayed.campusdock.UI.Canteen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sayed.campusdock.databinding.CanteenCartFragmentBinding

class CartFragment : Fragment() {

    private var _binding: CanteenCartFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CanteenCartFragmentBinding.inflate(inflater, container, false)











        return binding.root
    }


}