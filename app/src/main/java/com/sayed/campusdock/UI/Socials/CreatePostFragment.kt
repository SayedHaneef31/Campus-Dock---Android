package com.sayed.campusdock.UI.Socials

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
//import androidx.navigation.fragment.navGraphViewModels
import androidx.navigation.navGraphViewModels
import androidx.lifecycle.lifecycleScope
import com.sayed.campusdock.API.RetrofitClient
import com.sayed.campusdock.ConfigManager.TokenManager
import com.sayed.campusdock.Data.Socials.PostRequest
import com.sayed.campusdock.Data.Socials.TopicResponse
import com.sayed.campusdock.R
import com.sayed.campusdock.databinding.FragmentCreatePostBinding
import com.bumptech.glide.Glide
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class CreatePostFragment : Fragment() {

    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SocialPostsViewModel by navGraphViewModels(R.id.social_nav_graph)

    // Dynamic list to hold topics fetched from the API
    private var topics: List<TopicResponse> = emptyList()
    private var selectedTopicId: UUID? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ensure TokenManager is initialized before any access
        TokenManager.init(requireContext())

        // Fetch topics and then set up the spinner
        fetchTopics()

        // Live image preview
        binding.etPostImageUrl.addTextChangedListener { text ->
            val url = text?.toString()?.trim()
            if (!url.isNullOrEmpty()) {
                binding.cardImagePreview.visibility = View.VISIBLE
                Glide.with(this)
                    .load(url)
                    .centerCrop()
                    .into(binding.ivPreview)
            } else {
                binding.cardImagePreview.visibility = View.GONE
            }
        }

        binding.btnSubmitPost.setOnClickListener {
            submitPost()
        }
        
        binding.btnMenu.setOnClickListener {
            (activity as? com.sayed.campusdock.UI.Main.MainActivity)?.openDrawer()
        }
    }

    private fun fetchTopics() {
        val collegeIdString = TokenManager.getCollegeId()
        if (collegeIdString == null) {
            Toast.makeText(requireContext(), "College ID not found.", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.tilTopic.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val collegeId = UUID.fromString(collegeIdString)
                val response = RetrofitClient.instance.getAllTopicsByCollegeId(collegeId)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        topics = response.body()!!
                        setupTopicSpinner()
                    } else {
                        Log.e("CreatePostFragment", "Failed to fetch topics: ${response.code()}")
                        Toast.makeText(requireContext(), "Failed to load topics.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("CreatePostFragment", "Network request failed: ${e.message}")
                    Toast.makeText(requireContext(), "Network error. Failed to load topics.", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _binding?.let {
                        it.progressBar.visibility = View.GONE
                        it.tilTopic.isEnabled = true
                    }
                }
            }
        }
    }

    private fun setupTopicSpinner() {
        val topicNames = topics.map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, topicNames)
        binding.atvTopic.setAdapter(adapter)

        binding.atvTopic.setOnItemClickListener { _, _, position, _ ->
            selectedTopicId = topics[position].id
        }
    }

    private fun submitPost() {
        val title = binding.etPostTitle.text.toString().trim()
        val content = binding.etPostContent.text.toString().trim()
        val imageUrl = binding.etPostImageUrl.text.toString().trim()
        val isAnonymous = binding.switchIsAnonymous.isChecked
        val authorId = TokenManager.getUserId()
        val collegeId = TokenManager.getCollegeId()

        // Validate input
        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "Title cannot be empty.", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedTopicId == null) {
            Toast.makeText(requireContext(), "Please select a topic.", Toast.LENGTH_SHORT).show()
            return
        }
        if (authorId == null || collegeId == null) {
            Toast.makeText(requireContext(), "User or college ID not found.", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnSubmitPost.isEnabled = false

        val postRequest = PostRequest(
            title = title,
            content = content.ifEmpty { null },
            imageUrl = imageUrl.ifEmpty { null },
            authorId = UUID.fromString(authorId),
            topicId = selectedTopicId!!,
            collegeId = UUID.fromString(collegeId),
            isAnonymous = isAnonymous,
            isPoll = false
        )

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.createPost(postRequest)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(requireContext(), "Post created successfully!", Toast.LENGTH_SHORT).show()
                        // Proactively refresh cached lists so tabs show the new post
                        val collegeIdUuid = UUID.fromString(collegeId)
                        viewModel.refreshAllPosts(collegeIdUuid)
                        viewModel.refreshTrendingPosts(collegeIdUuid)
                        // Restore UI state before navigating away to avoid binding NPE in finally
                        _binding?.let {
                            it.progressBar.visibility = View.GONE
                            it.btnSubmitPost.isEnabled = true
                        }
                        findNavController().navigate(R.id.action_createPostFragment_to_socialFragment2)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("CreatePostFragment", "Failed to create post: ${response.code()} - $errorBody")
                        Toast.makeText(requireContext(), "Failed to create post. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("CreatePostFragment", "Network request failed: ${e.message}")
                    Toast.makeText(requireContext(), "Network error. Please check your connection.", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _binding?.let {
                        it.progressBar.visibility = View.GONE
                        it.btnSubmitPost.isEnabled = true
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
