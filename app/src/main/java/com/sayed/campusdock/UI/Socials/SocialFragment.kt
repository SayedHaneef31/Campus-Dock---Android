package com.sayed.campusdock.UI.Socials

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sayed.campusdock.API.RetrofitClient
import com.sayed.campusdock.ConfigManager.TokenManager
import com.sayed.campusdock.R
import kotlinx.coroutines.launch
import java.util.UUID

class SocialFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.socials_fragment, container, false)

        val viewPager: ViewPager2 = view.findViewById(R.id.viewPager)
        val tabLayout: TabLayout = view.findViewById(R.id.tabLayout)
        val fabAddPost: FloatingActionButton = view.findViewById(R.id.fabAddPost)
        val imgProfile: ImageView = view.findViewById(R.id.imgProfile)
        val btnMenu: View = view.findViewById(R.id.btnMenu)

        // Load profile picture
        loadProfilePicture(imgProfile)

        // Set up the ViewPager with a custom adapterff
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

        fabAddPost.setOnClickListener {
            findNavController().navigate(R.id.action_socialFragment_to_createPostFragment)
        }

        imgProfile.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }
        
        btnMenu.setOnClickListener {
            (activity as? com.sayed.campusdock.UI.Main.MainActivity)?.openDrawer()
        }

        return view
    }

    private fun loadProfilePicture(imgProfile: ImageView) {
        // Use cached URL if available
        val cachedUrl = TokenManager.getProfilePicUrl()
        if (!cachedUrl.isNullOrEmpty()) {
            Glide.with(requireContext())
                .load(cachedUrl)
                .placeholder(R.drawable.profile_pic)
                .error(R.drawable.profile_pic)
                .circleCrop()
                .into(imgProfile)
            return
        }

        // Fetch from API if not cached
        val userIdStr = TokenManager.getUserId()
        val userUuid = try { userIdStr?.let { UUID.fromString(it) } } catch (_: Exception) { null }

        if (userUuid != null) {
            lifecycleScope.launch {
                try {
                    val resp = RetrofitClient.instance.getUserById(userUuid)
                    if (resp.isSuccessful) {
                        resp.body()?.profilePicUrl?.let { url ->
                            TokenManager.setProfilePicUrl(url) // Cache it
                            Glide.with(requireContext())
                                .load(url)
                                .placeholder(R.drawable.profile_pic)
                                .error(R.drawable.profile_pic)
                                .circleCrop()
                                .into(imgProfile)
                        }
                    }
                } catch (_: Exception) {
                    // Keep default image on error
                }
            }
        }
    }

    private inner class ViewPagerAdapter(
        fragment: Fragment
    ) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 3

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