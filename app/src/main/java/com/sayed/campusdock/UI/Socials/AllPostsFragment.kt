package com.sayed.campusdock.UI.Socials

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sayed.campusdock.API.RetrofitClient
import com.sayed.campusdock.Adaptor.PostAdapter
import com.sayed.campusdock.ConfigManager.TokenManager
import com.sayed.campusdock.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class AllPostsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private val viewModel: SocialPostsViewModel by navGraphViewModels(R.id.social_nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_posts, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewAllPosts)
        recyclerView.layoutManager = LinearLayoutManager(context)

        postAdapter = PostAdapter(emptyList()) { postId ->
            // No-op for now, will be updated in fetchPosts
        }
        recyclerView.adapter = postAdapter

        // Initialize TokenManager and get the college ID
        TokenManager.init(requireContext())
        val collegeIdString = TokenManager.getCollegeId()

        if (collegeIdString != null) {
            val collegeId = UUID.fromString(collegeIdString)
            // Observe cached posts
            viewModel.allPosts.observe(viewLifecycleOwner) { posts ->
                postAdapter = PostAdapter(posts) { postId ->
                    val action = SocialFragmentDirections.actionSocialFragmentToPostScreenFragment(postId.toString())
                    findNavController().navigate(action)
                }
                recyclerView.adapter = postAdapter
            }
            // Trigger initial load only if needed
            viewModel.ensureAllPosts(collegeId)
        } else {
            Log.e("AllPostsFragment", "Could not get collegeId from TokenManager.")
            Toast.makeText(context, "Could not get college ID", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    
}