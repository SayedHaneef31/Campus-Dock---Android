package com.sayed.campusdock.UI.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.sayed.campusdock.Adaptor.BannerAdapter
import com.sayed.campusdock.R
import com.sayed.campusdock.databinding.HomeFragmentBinding
import kotlin.math.abs

class HomeFragment : Fragment() {

    //Enable view binding
    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        enableBannerView()
        return binding.root
    }

    private fun enableBannerView() {
        val banners = listOf(
            R.drawable.genero_img,
            R.drawable.banner2,
            R.drawable.banner5,
            R.drawable.banner6,
            R.drawable.banner7,
            R.drawable.banner3,
            R.drawable.banner4
        )
        val adapter = BannerAdapter(banners)
        binding.bannerViewPager.adapter = adapter

        // Set offscreenPageLimit to 1 to show adjacent pages
        binding.bannerViewPager.offscreenPageLimit = 1

        val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.pageMargin)
        val pageOffsetPx = resources.getDimensionPixelOffset(R.dimen.pageOffset)

        // CompositePageTransformer allows us to apply multiple transformations.
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(pageMarginPx))


        // This custom transformer handles the centering and scaling.
        val customTransformer = ViewPager2.PageTransformer { page, position ->
            val scaleFactor = 0.85f.coerceAtLeast(1 - abs(position) * 0.15f)
            val alphaFactor = 0.7f.coerceAtLeast(1 - abs(position) * 0.3f)

            page.scaleY = scaleFactor
            page.scaleX = scaleFactor
            page.alpha = alphaFactor

            // Correctly align the center page while shifting the others.
            // This formula is the key to making adjacent cards visible.
            val translationOffset = (position * -(2 * pageOffsetPx + pageMarginPx))
            page.translationX = translationOffset


        }

        compositePageTransformer.addTransformer(customTransformer)

        binding.bannerViewPager.setPageTransformer(compositePageTransformer)
        //set the initial position
        binding.bannerViewPager.setCurrentItem(3, false)

    }
        override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}