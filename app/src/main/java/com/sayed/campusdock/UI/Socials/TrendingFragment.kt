package com.sayed.campusdock.UI.Socials

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sayed.campusdock.R
import com.sayed.campusdock.Adaptor.PostAdapter
import com.sayed.campusdock.Data.Socials.Post
import java.time.LocalDateTime
import java.util.UUID

class TrendingFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trending, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewAllPosts) // Use the same RecyclerView ID
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Create a list of mock posts for the Trending section
        val mockPosts = generateMockTrendingPosts()

        val postAdapter = PostAdapter(mockPosts)
        recyclerView.adapter = postAdapter

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateMockTrendingPosts(): List<Post> {
        return listOf(
            Post(
                id = UUID.randomUUID(),
                topicName = "General",
                title = "What's your favorite study spot on campus?",
                content = null,
                imageUrl = "https://images.unsplash.com/photo-1588070566367-9d7a8e7a83d7",
                authorUsername = "StudentBuddy",
                authorId = UUID.randomUUID(),
                isAnonymous = false,
                upvoteCount = 250,
                downvoteCount = 10,
                commentCount = 120,
                createdAt = LocalDateTime.now().minusHours(5)
            ),
            Post(
                id = UUID.randomUUID(),
                topicName = "Academics",
                title = "Let's settle once and for all! JAVA V/S C++",
                content = "Which language is better for software development and why? Let's discuss the pros and cons of each.",
                imageUrl = "https://images.unsplash.com/photo-1596707328608-f46328608c02",
                authorUsername = "TechTudumm",
                authorId = UUID.randomUUID(),
                isAnonymous = false,
                upvoteCount = 300,
                downvoteCount = 50,
                commentCount = 200,
                createdAt = LocalDateTime.now().minusHours(1)
            ),
            Post(
                id = UUID.randomUUID(),
                topicName = "Poll",
                title = "Should the library be open 24/7 during finals?",
                content = null,
                imageUrl = null, // Example of a post with no image
                authorUsername = "StudentGovernment",
                authorId = UUID.randomUUID(),
                isAnonymous = false,
                upvoteCount = 500,
                downvoteCount = 20,
                commentCount = 75,
                createdAt = LocalDateTime.now().minusHours(6)
            )
        )
    }
}