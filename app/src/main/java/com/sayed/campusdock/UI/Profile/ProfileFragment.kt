package com.sayed.campusdock.UI.Profile

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.sayed.campusdock.R
import com.sayed.campusdock.databinding.ProfileFragmentBinding
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.sayed.campusdock.API.RetrofitClient
import com.sayed.campusdock.ConfigManager.TokenManager
import com.sayed.campusdock.Data.Marketplace.Product
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.UUID
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: ProfileFragmentBinding? = null
    private val binding get() = _binding!!

    private val userListings = mutableListOf<Product>()
    private var selectedImageUri: Uri? = null
    private var currentUserId: UUID? = null
    private var currentProfilePicUrl: String? = null

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            uploadProfilePicture(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load default profile image initially
        binding.profileImage.setImageResource(R.drawable.profile_pic)

        // Prefer fresh profile via API using userId from token
        val userIdStr = TokenManager.getUserId()
        val userUuid = try { userIdStr?.let { UUID.fromString(it) } } catch (_: Exception) { null }
        currentUserId = userUuid
        if (userUuid != null) {
            lifecycleScope.launch {
                try {
                    val resp = RetrofitClient.instance.getUserById(userUuid)
                    if (resp.isSuccessful) {
                        resp.body()?.let { user ->
                            val rawName = user.name
                            binding.profileName.text = user.name.uppercase()
                            binding.profileEmail.text = user.email
                            binding.profileHighlightTitle.text = "${user.name.split(" ").first()}'s Market Listings"
                            // Load profile picture if available
                            user.profilePicUrl?.let { url ->
                                currentProfilePicUrl = url
                                loadProfileImage(url)
                            }
                        }
                    } else {
                        populateFromJwtFallback()
                    }
                } catch (_: Exception) {
                    populateFromJwtFallback()
                }
            }
        } else {
            populateFromJwtFallback()
        }

        fetchUserListings()

        setupClickListeners()
        setupOrderHistorySection()
        setupSocialPostsSection()
    }

    private fun populateFromJwtFallback() {
        val name = TokenManager.getName()
        val email = TokenManager.getEmail()

        // Only set if current text is empty (don't overwrite API-fetched data)
        if (name != null && binding.profileName.text.isNullOrBlank()) {
            binding.profileName.text = name.uppercase()
            binding.profileHighlightTitle.text = "${name.split(" ").first()}'s Market Listings"
        }
        if (email != null && binding.profileEmail.text.isNullOrBlank()) {
            binding.profileEmail.text = email
        }
    }

    private fun setupClickListeners() {
        // Settings button click
        binding.btnSettings?.setOnClickListener {
            Toast.makeText(context, "Settings coming soon", Toast.LENGTH_SHORT).show()
        }

        // Profile picture click - open image picker
        binding.profileImageOverlay.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
    }

    private fun loadProfileImage(url: String, skipPlaceholder: Boolean = false) {
        val requestBuilder = Glide.with(requireContext())
            .load(url)
            .circleCrop()

        if (!skipPlaceholder) {
            requestBuilder.placeholder(R.drawable.profile_pic)
        }

        requestBuilder.error(R.drawable.profile_pic)
            .into(binding.profileImage)
    }

    private fun uploadProfilePicture(uri: Uri, retryCount: Int = 0) {
        val userId = currentUserId ?: run {
            Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            // Show loading spinner
            binding.profileUploadProgress.visibility = View.VISIBLE
            binding.profileImageOverlay.visibility = View.GONE // Hide edit button during upload

            try {
                android.util.Log.d("ProfileUpload", "Starting upload for URI: $uri (attempt ${retryCount + 1})")

                val file = uriToFile(uri) ?: run {
                    Toast.makeText(context, "Failed to process image", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                android.util.Log.d("ProfileUpload", "File created: ${file.absolutePath}, size: ${file.length()} bytes")

                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                android.util.Log.d("ProfileUpload", "Sending to server...")
                val response = RetrofitClient.instance.updateProfilePicture(userId, body)

                if (response.isSuccessful) {
                    response.body()?.let { uploadResponse ->
                        currentProfilePicUrl = uploadResponse.profilePicUrl
                        loadProfileImage(uploadResponse.profilePicUrl, skipPlaceholder = true)
                        Toast.makeText(context, "Profile picture updated!", Toast.LENGTH_SHORT).show()
                        android.util.Log.d("ProfileUpload", "Success: ${uploadResponse.profilePicUrl}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("ProfileUpload", "Error ${response.code()}: $errorBody")

                    // Auto-retry on server error (500) up to 2 times
                    if (response.code() == 500 && retryCount < 2) {
                        android.util.Log.d("ProfileUpload", "Retrying upload (attempt ${retryCount + 2})...")
                        delay(1000) // Wait 1 second before retry
                        uploadProfilePicture(uri, retryCount + 1)
                        return@launch // Exit early, retry will handle cleanup
                    } else {
                        Toast.makeText(context, "Upload failed (${response.code()}): ${errorBody ?: response.message()}", Toast.LENGTH_LONG).show()
                    }
                }

                // Clean up temp file
                file.delete()
            } catch (e: Exception) {
                android.util.Log.e("ProfileUpload", "Exception: ${e.message}", e)
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                // Hide loading spinner and show edit button
                binding.profileUploadProgress.visibility = View.GONE
                binding.profileImageOverlay.visibility = View.VISIBLE
            }
        }
    }

    private fun uriToFile(uri: Uri): File? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri) ?: return null
            val file = File(requireContext().cacheDir, "temp_profile_pic_${System.currentTimeMillis()}.jpg")
            file.outputStream().use { output ->
                inputStream.copyTo(output)
            }
            file
        } catch (e: Exception) {
            null
        }
    }

    private fun fetchUserListings() {
        lifecycleScope.launch {
            try {
                val collegeId = TokenManager.getCollegeId()
                val userId = TokenManager.getUserId()

                if (userId == null) {
                    showEmptyListings()
                    return@launch
                }

                val response = RetrofitClient.instance.getProducts(
                    page = 0,
                    size = 20,
                    collegeId = collegeId,
                    userId = userId
                )

                userListings.clear()
                userListings.addAll(response.content.map { dto ->
                    Product(
                        id = null,
                        name = dto.name,
                        price = "₹${dto.price.toInt()}",
                        imageUrl = dto.urls?.firstOrNull(),
                        sellerName = dto.userName,
                        description = dto.description
                    )
                })

                populateListings()
            } catch (e: Exception) {
                showEmptyListings()
            }
        }
    }

    private fun showEmptyListings() {
        setupExpandableSection(
            section = binding.sectionMarketplace.root,
            title = "Marketplace Listings",
            items = listOf(MarketplaceItem("No listings yet", "Tap + in marketplace to add your first listing", "", 0))
        )
    }

    private fun populateListings() {
        val items = if (userListings.isEmpty()) {
            listOf(MarketplaceItem("No listings yet", "Tap + in marketplace to add your first listing", "", 0))
        } else {
            userListings.map { MarketplaceItem(it.name, "Posted by you", it.price, 0) }
        }
        setupExpandableSection(
            section = binding.sectionMarketplace.root,
            title = "Marketplace Listings",
            items = items
        )
    }

    private fun setupOrderHistorySection() {
        val orderHistoryItems = listOf(
            OrderHistoryItem("TeaMan's Cafe", "Order #12345", "₹80", R.drawable.bhatura),
            OrderHistoryItem("TeaMan's Cafe", "Order #67890", "₹135", R.drawable.paratha)
        )
        setupExpandableSection(
            section = binding.sectionOrderHistory.root,
            title = "Order History",
            items = orderHistoryItems
        )
    }

    private fun setupSocialPostsSection() {
        val socialPostItems = listOf(
            SocialPostItem("Poll - Student Union", "Posted 3 days ago", R.drawable.student_union),
            SocialPostItem("Post - Food Discussion", "Posted 1 week ago", R.drawable.pav)
        )
        setupExpandableSection(
            section = binding.sectionSocialPosts.root,
            title = "Social Posts",
            items = socialPostItems
        )
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
                    inflater.inflate(R.layout.profile_item_order_history, content, false).apply {
                        findViewById<ImageView>(R.id.orderImage).setImageResource(item.imageResId)
                        findViewById<TextView>(R.id.orderTitle).text = item.title
                        findViewById<TextView>(R.id.orderDescription).text = item.description
                        findViewById<TextView>(R.id.orderPrice).text = item.price
                    }
                }
                is MarketplaceItem -> {
                    inflater.inflate(R.layout.profile_item_order_history, content, false).apply {
                        findViewById<ImageView>(R.id.orderImage).setImageResource(
                            if (item.imageResId != 0) item.imageResId else R.drawable.keyboard
                        )
                        findViewById<TextView>(R.id.orderTitle).text = item.title
                        findViewById<TextView>(R.id.orderDescription).text = item.description
                        findViewById<TextView>(R.id.orderPrice).text = item.price
                    }
                }
                is SocialPostItem -> {
                    inflater.inflate(R.layout.profile_item_social_post, content, false).apply {
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