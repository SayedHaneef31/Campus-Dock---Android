package com.sayed.campusdock.UI.Socials

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.sayed.campusdock.API.RetrofitClient
import com.sayed.campusdock.Data.Socials.Post
import com.sayed.campusdock.R

import com.sayed.campusdock.databinding.SocialsFragmentPostScreenBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import com.sayed.campusdock.ConfigManager.TokenManager
import com.sayed.campusdock.Data.Socials.VoteType
import androidx.core.content.ContextCompat



class PostScreenFragment : Fragment() {

    private val args: PostScreenFragmentArgs by navArgs()
    private var _binding: SocialsFragmentPostScreenBinding? = null
    private val binding get() = _binding!!
    private var currentPost: Post? = null
    private var currentVoteState: VoteType? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SocialsFragmentPostScreenBinding.inflate(inflater, container, false)
        val view = binding.root

        togglePostLoading(true)
        // Fetch the post details from the API
        fetchPostDetails(args.postId)
        
        binding.btnMenu.setOnClickListener {
            (activity as? com.sayed.campusdock.UI.Main.MainActivity)?.openDrawer()
        }

        // Set up vote click listeners
        binding.ivUpvote.setOnClickListener {
            currentPost?.let { post ->
                val userId = TokenManager.getUserId()
                if (userId != null) {
                    voteOnPost(post.id, UUID.fromString(userId), VoteType.UPVOTE)
                } else {
                    Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.ivDownvote.setOnClickListener {
            currentPost?.let { post ->
                val userId = TokenManager.getUserId()
                if (userId != null) {
                    voteOnPost(post.id, UUID.fromString(userId), VoteType.DOWNVOTE)
                } else {
                    Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }

    private fun fetchPostDetails(postId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getPostById(UUID.fromString(postId))
                if (response.isSuccessful) {
                    val post = response.body()
                    if (post != null) {
                        withContext(Dispatchers.Main) {
                            // Populate the UI with the fetched data
                            bindPostDataToUI(post)
                            togglePostLoading(false)
                        }
                    } else {
                        Log.e("PostScreenFragment", "Post body is null")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Post not found.", Toast.LENGTH_SHORT).show()
                            togglePostLoading(false)
                        }
                    }
                } else {
                    Log.e("PostScreenFragment", "API call failed with code: ${response.code()}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Failed to fetch post details.", Toast.LENGTH_SHORT).show()
                        togglePostLoading(false)
                    }
                }
            } catch (e: Exception) {
                Log.e("PostScreenFragment", "Network request failed: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Network request failed.", Toast.LENGTH_SHORT).show()
                    togglePostLoading(false)
                }
            }
        }
    }

    private fun bindPostDataToUI(post: Post) {
        currentPost = post
        // Set up the post-specific data using the binding object
        binding.tvUserName.text = if (post.isAnonymous) post.authorAnonymousName ?: "Anonymous" else post.authorName
        binding.tvPostedIn.text = "Posted in: ${post.topicName}"
        binding.tvPostTitle.text = post.title
        binding.tvUpvoteCount.text = post.upvoteCount.toString()
        binding.tvDownvoteCount.text = post.downvoteCount.toString()
        binding.tvPostBody.text = post.content ?: "No content available."

        // Handle the post image
        if (!post.imageUrl.isNullOrEmpty()) {
            binding.cardPostImage.visibility = View.VISIBLE
            Glide.with(requireContext())
                .load(post.imageUrl)
                .placeholder(R.drawable.student_union)
                .into(binding.ivPostImage)
        } else {
            binding.cardPostImage.visibility = View.GONE
        }

        // Update vote UI
        updateVoteUI()
    }

    private fun togglePostLoading(isLoading: Boolean) {
        binding.postShimmerContainer.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.scrollView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun voteOnPost(postId: UUID, userId: UUID, voteType: VoteType) {
        // Handle toggling off same vote
        if (currentVoteState == voteType) {
            currentVoteState = null
            updateVoteUI()
            // Still call API to remove vote
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    RetrofitClient.instance.voteOnPost(postId, userId, voteType.name)
                } catch (e: Exception) {
                    Log.e("PostScreenFragment", "Vote removal failed: ${e.message}")
                }
            }
            return
        }

        // Store previous vote state
        val previousVoteState = currentVoteState
        val previousUpvoteCount = binding.tvUpvoteCount.text.toString().toIntOrNull() ?: 0
        val previousDownvoteCount = binding.tvDownvoteCount.text.toString().toIntOrNull() ?: 0

        // Update vote state
        currentVoteState = voteType

        // Optimistic update - adjust counts based on vote type and previous state
        when (voteType) {
            VoteType.UPVOTE -> {
                var newUpvoteCount = previousUpvoteCount + 1
                var newDownvoteCount = previousDownvoteCount
                
                // If previously downvoted, remove that vote
                if (previousVoteState == VoteType.DOWNVOTE) {
                    newDownvoteCount = maxOf(0, previousDownvoteCount - 1)
                }
                
                binding.tvUpvoteCount.text = newUpvoteCount.toString()
                binding.tvDownvoteCount.text = newDownvoteCount.toString()
            }
            VoteType.DOWNVOTE -> {
                var newDownvoteCount = previousDownvoteCount + 1
                var newUpvoteCount = previousUpvoteCount
                
                // If previously upvoted, remove that vote
                if (previousVoteState == VoteType.UPVOTE) {
                    newUpvoteCount = maxOf(0, previousUpvoteCount - 1)
                }
                
                binding.tvUpvoteCount.text = newUpvoteCount.toString()
                binding.tvDownvoteCount.text = newDownvoteCount.toString()
            }
        }

        updateVoteUI()

        // Make API call in background
        CoroutineScope(Dispatchers.IO).launch {
            try {
                RetrofitClient.instance.voteOnPost(postId, userId, voteType.name)
            } catch (e: Exception) {
                Log.e("PostScreenFragment", "Voting failed: ${e.message}")
                withContext(Dispatchers.Main) {
                    // Revert on error
                    currentVoteState = previousVoteState
                    binding.tvUpvoteCount.text = previousUpvoteCount.toString()
                    binding.tvDownvoteCount.text = previousDownvoteCount.toString()
                    updateVoteUI()
                    Toast.makeText(context, "Failed to vote", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateVoteUI() {
        val greenColor = ContextCompat.getColor(requireContext(), R.color.green_dark)
        val redColor = ContextCompat.getColor(requireContext(), R.color.red)
        val grayColor = ContextCompat.getColor(requireContext(), R.color.gray)

        when (currentVoteState) {
            VoteType.UPVOTE -> {
                binding.ivUpvote.setColorFilter(greenColor)
                binding.tvUpvoteCount.setTextColor(greenColor)
                binding.ivDownvote.setColorFilter(grayColor)
                binding.tvDownvoteCount.setTextColor(grayColor)
            }
            VoteType.DOWNVOTE -> {
                binding.ivDownvote.setColorFilter(redColor)
                binding.tvDownvoteCount.setTextColor(redColor)
                binding.ivUpvote.setColorFilter(grayColor)
                binding.tvUpvoteCount.setTextColor(grayColor)
            }
            null -> {
                binding.ivUpvote.setColorFilter(grayColor)
                binding.tvUpvoteCount.setTextColor(grayColor)
                binding.ivDownvote.setColorFilter(grayColor)
                binding.tvDownvoteCount.setTextColor(grayColor)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}