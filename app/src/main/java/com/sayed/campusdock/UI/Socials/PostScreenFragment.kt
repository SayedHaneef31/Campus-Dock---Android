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
import com.sayed.campusdock.databinding.FragmentPostScreenBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID



class PostScreenFragment : Fragment() {

    private val args: PostScreenFragmentArgs by navArgs()
    private var _binding: FragmentPostScreenBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostScreenBinding.inflate(inflater, container, false)
        val view = binding.root

        // Fetch the post details from the API
        fetchPostDetails(args.postId)

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
                        }
                    } else {
                        Log.e("PostScreenFragment", "Post body is null")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Post not found.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("PostScreenFragment", "API call failed with code: ${response.code()}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Failed to fetch post details.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("PostScreenFragment", "Network request failed: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Network request failed.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun bindPostDataToUI(post: Post) {
        // Set up the post-specific data using the binding object
        binding.tvUserName.text = if (post.isAnonymous) post.authorAnonymousName ?: "Anonymous" else post.authorName
        binding.tvPostedIn.text = "Posted in: ${post.topicName}"
        binding.tvPostTitle.text = post.title
        binding.tvUpvoteCount.text = post.upvoteCount.toString()
        binding.tvDownvoteCount.text = post.downvoteCount.toString()
        binding.tvPostBody.text = post.content ?: "No content available."

        // Handle the post image
        if (!post.imageUrl.isNullOrEmpty()) {
            binding.ivPostImage.visibility = View.VISIBLE
            Glide.with(requireContext())
                .load(post.imageUrl)
                .placeholder(R.drawable.student_union)
                .into(binding.ivPostImage)
        } else {
            binding.ivPostImage.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}