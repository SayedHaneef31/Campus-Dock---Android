package com.sayed.campusdock.UI.Socials

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sayed.campusdock.R
import com.sayed.campusdock.Adaptor.PostAdapter
import com.sayed.campusdock.ConfigManager.TokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class PollsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.socials_fragment_polls, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewAllPosts)
        recyclerView.layoutManager = LinearLayoutManager(context)

        postAdapter = PostAdapter(
            emptyList(),
            onPostClick = { postId -> /* No-op for now */ },
            onVote = { _, _, _ -> /* No-op for now */ }
        )
        recyclerView.adapter = postAdapter

        // Initialize TokenManager and get the college ID
        TokenManager.init(requireContext())
        val collegeIdString = TokenManager.getCollegeId()

        // Fetch posts using the college ID
        if (collegeIdString != null) {
            fetchPosts(UUID.fromString(collegeIdString))
        } else {
            Toast.makeText(context, "Could not get college ID", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun fetchPosts(collegeId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                // Assuming there's a specific API endpoint for polls.
//                // If not, you may need to filter posts after fetching all.
//                // Replace with the correct API call if available.
//                val response = RetrofitClient.instance.getAllPostsByCollegeId(collegeId)
//                if (response.isSuccessful) {
//                    val posts = response.body() ?: emptyList()
//                    withContext(Dispatchers.Main) {
//                        postAdapter = PostAdapter(posts) { postId ->
//                            val action = SocialFragmentDirections.actionSocialFragmentToPostScreenFragment(postId.toString())
//                            findNavController().navigate(action)
//                        }
//                        recyclerView.adapter = postAdapter
//                    }
//                } else {
//                    withContext(Dispatchers.Main) {
//                        Toast.makeText(context, "Failed to fetch polls", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(context, "Network request failed", Toast.LENGTH_SHORT).show()
//                }
//            }
        }
    }
}