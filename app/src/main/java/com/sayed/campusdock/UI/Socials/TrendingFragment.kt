package com.sayed.campusdock.UI.Socials

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sayed.campusdock.Adaptor.PostAdapter
import com.sayed.campusdock.ConfigManager.TokenManager
import com.sayed.campusdock.R
import java.util.UUID

class TrendingFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private lateinit var emptyView: TextView
    private lateinit var shimmerContainer: View
    private val viewModel: SocialPostsViewModel by navGraphViewModels(R.id.social_nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.socials_fragment_trending, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewAllPosts)
        emptyView = view.findViewById(R.id.emptyView)
        shimmerContainer = view.findViewById(R.id.shimmerContainer)
        recyclerView.layoutManager = LinearLayoutManager(context)

        postAdapter = PostAdapter(
            emptyList(),
            onPostClick = { postId -> /* No-op for now */ },
            onVote = { _, _, _ -> /* No-op for now */ }
        )
        recyclerView.adapter = postAdapter

        TokenManager.init(requireContext())
        val collegeIdString = TokenManager.getCollegeId()
        val userIdString = TokenManager.getUserId()

        if (collegeIdString != null && userIdString != null) {
            val collegeId = UUID.fromString(collegeIdString)
            val userId = UUID.fromString(userIdString)
            viewModel.trendingPosts.observe(viewLifecycleOwner) { posts ->
                postAdapter = PostAdapter(
                    posts,
                    onPostClick = { postId ->
                        val action = SocialFragmentDirections.actionSocialFragmentToPostScreenFragment(postId.toString())
                        findNavController().navigate(action)
                    },
                    onVote = { postId, _, voteType ->
                        viewModel.voteOnPost(postId, userId, voteType, collegeId)
                    }
                )
                recyclerView.adapter = postAdapter
                if (posts.isNullOrEmpty()) {
                    crossfade(recyclerView, false)
                    emptyView.visibility = View.VISIBLE
                } else {
                    emptyView.visibility = View.GONE
                    crossfade(recyclerView, true)
                }
            }
            viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
                shimmerContainer.visibility = if (loading) View.VISIBLE else View.GONE
                recyclerView.visibility = if (loading) View.GONE else View.VISIBLE
                if (loading) emptyView.visibility = View.GONE
            }
            viewModel.ensureTrendingPosts(collegeId)
        } else {
            Log.e("TrendingFragment", "Could not get collegeId or userId from TokenManager.")
            Toast.makeText(context, "Could not get user or college ID", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun crossfade(target: View, show: Boolean) {
        if (show) {
            target.alpha = 0f
            target.visibility = View.VISIBLE
            target.animate().alpha(1f).setDuration(180).start()
        } else {
            target.animate().alpha(0f).setDuration(150).withEndAction {
                target.visibility = View.GONE
            }.start()
        }
    }

}