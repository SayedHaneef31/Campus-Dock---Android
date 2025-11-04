package com.sayed.campusdock.UI.Profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.util.Base64
import org.json.JSONObject
import androidx.navigation.fragment.findNavController
import com.sayed.campusdock.R
import com.sayed.campusdock.databinding.ProfileFragmentBinding
import androidx.lifecycle.lifecycleScope
import com.sayed.campusdock.API.RetrofitClient
import com.sayed.campusdock.ConfigManager.TokenManager
import java.util.UUID
import kotlinx.coroutines.launch
import java.util.Locale

class ProfileFragment : Fragment() {

    private var _binding: ProfileFragmentBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfileFragmentBinding.inflate(inflater, container, false)



        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }




        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Prefer fresh profile via API using userId from token
        val userIdStr = TokenManager.getUserId()
        val userUuid = try { userIdStr?.let { UUID.fromString(it) } } catch (_: Exception) { null }
        if (userUuid != null) {
            lifecycleScope.launch {
                try {
                    val resp = RetrofitClient.instance.getUserById(userUuid)
                    if (resp.isSuccessful) {
                        resp.body()?.let { user ->
                            val rawName = user.name
                            binding.profileName.text = rawName.uppercase()
                            binding.profileEmail.text = user.email
                            binding.profileHighlightTitle.text = "\uD83C\uDF1F ${rawName.uppercase()}'s Highlight \uD83C\uDF1F"
                        }
                    } else {
                        // Fallback to JWT payload if API fails
                        populateFromJwtFallback()
                    }
                } catch (_: Exception) {
                    populateFromJwtFallback()
                }
            }
        } else {
            // Fallback if userId missing
            populateFromJwtFallback()
        }

        // Data for each section
        val orderHistoryItems = listOf(
            OrderHistoryItem("TeaMans's Cafe", "Order #12345", "₹80", R.drawable.bhatura),
            OrderHistoryItem("TeaMans's Cafe", "Order #67890", "₹135", R.drawable.aalu_paratha)
        )

        val marketplaceItems = listOf(
            MarketplaceItem("Zebronics Mechanical Keyboard 6U", "Posted 2 days ago", "₹800", R.drawable.keyboard),
            MarketplaceItem("Graphics Drafter", "Posted 1 week ago", "₹250", R.drawable.drafter)
        )

        val socialPostItems = listOf(
            SocialPostItem("Poll - Student Union", "Posted 3 days ago", R.drawable.student_union),
            SocialPostItem("Post - Food Discusion", "Posted 1 week ago", R.drawable.pav)
        )

        setupExpandableSection(
            section = binding.sectionOrderHistory.root,
            title = "Order History",
            items = orderHistoryItems
        )
        setupExpandableSection(
            section = binding.sectionMarketplace.root,
            title = "Marketplace Listings",
            items = marketplaceItems
        )
        setupExpandableSection(
            section = binding.sectionSocialPosts.root,
            title = "Social Posts",
            items = socialPostItems
        )

    }

    private fun populateFromJwtFallback() {
        val prefs = requireContext().getSharedPreferences("CampusDockPrefs", android.content.Context.MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)
        if (!token.isNullOrEmpty()) {
            try {
                val parts = token.split(".")
                if (parts.size == 3) {
                    val payloadJson = String(Base64.decode(parts[1], Base64.URL_SAFE))
                    val payload = JSONObject(payloadJson)
                    val name = payload.optString("name", "")
                    val email = payload.optString("email", "")
                    val displayName = when {
                        name.isNotBlank() -> name
                        email.contains("@") -> email.substringBefore("@").replaceFirstChar { it.uppercase() }
                        else -> "User"
                    }
                    binding.profileName.text = displayName.uppercase()
                    if (email.isNotBlank()) binding.profileEmail.text = email
                    binding.profileHighlightTitle.text = "\uD83C\uDF1F ${displayName}'s Highlight \uD83C\uDF1F"
                }
            } catch (_: Exception) { /* ignore */ }
        }
    }

    private fun setupExpandableSection(section: View, title: String, items: List<Any>) {
        val header = section.findViewById<LinearLayout>(R.id.headerLayout)
        val sectionTitle = section.findViewById<TextView>(R.id.sectionTitle)
        val arrow = section.findViewById<ImageView>(R.id.arrowIcon)
        val content = section.findViewById<LinearLayout>(R.id.contentLayout)

        sectionTitle.text = title
        content.removeAllViews()

        val inflater = LayoutInflater.from(requireContext())

        for (item in items) {
            val listItemView: View = when (item) {
                is OrderHistoryItem -> {
                    inflater.inflate(R.layout.item_order_history, content, false).apply {
                        findViewById<ImageView>(R.id.orderImage).setImageResource(item.imageResId)
                        findViewById<TextView>(R.id.orderTitle).text = item.title
                        findViewById<TextView>(R.id.orderDescription).text = item.description
                        findViewById<TextView>(R.id.orderPrice).text = item.price
                    }
                }
                is MarketplaceItem -> {
                    inflater.inflate(R.layout.item_order_history, content, false).apply {
                        findViewById<ImageView>(R.id.orderImage).setImageResource(item.imageResId)
                        findViewById<TextView>(R.id.orderTitle).text = item.title
                        findViewById<TextView>(R.id.orderDescription).text = item.description
                        findViewById<TextView>(R.id.orderPrice).text = item.price
                    }
                }
                is SocialPostItem -> {
                    inflater.inflate(R.layout.item_social_post, content, false).apply {
                        findViewById<ImageView>(R.id.postImage).setImageResource(item.imageResId)
                        findViewById<TextView>(R.id.postTitle).text = item.title
                        findViewById<TextView>(R.id.postDescription).text = item.description
                    }
                }
                else -> View(requireContext())
            }
            content.addView(listItemView)
        }

        header.setOnClickListener {
            if (content.visibility == View.GONE) {
                content.visibility = View.VISIBLE
                arrow.setImageResource(R.drawable.up)
            } else {
                content.visibility = View.GONE
                arrow.setImageResource(R.drawable.down)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Data classes to hold information for each item
    data class OrderHistoryItem(val title: String, val description: String, val price: String, val imageResId: Int)
    data class MarketplaceItem(val title: String, val description: String, val price: String, val imageResId: Int)
    data class SocialPostItem(val title: String, val description: String, val imageResId: Int)


}