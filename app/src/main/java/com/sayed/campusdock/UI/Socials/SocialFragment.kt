package com.sayed.campusdock.UI.Socials

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sayed.campusdock.R

class SocialFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.social_fragment, container, false)

        val viewPager: ViewPager2 = view.findViewById(R.id.viewPager)
        val tabLayout: TabLayout = view.findViewById(R.id.tabLayout)

        // Set up the ViewPager with a custom adapter
        val pagerAdapter = ViewPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        // Connect the TabLayout to the ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "All Posts"
                1 -> "Trending"
                2 -> "Polls"
                else -> throw IllegalStateException("Invalid tab position")
            }
        }.attach()

        return view
    }

    /**
     * Adapter to manage fragments in the ViewPager2.
     * Each position corresponds to a different tab.
     */
    private inner class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 3 // We have 3 tabs: All Posts, Trending, Polls

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> AllPostsFragment()
                1 -> TrendingFragment()
                2 -> PollsFragment()
                else -> throw IllegalArgumentException("Invalid position")
            }
        }
    }
}