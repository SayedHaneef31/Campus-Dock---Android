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

class AllPostsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_posts, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewAllPosts)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Create a list of mock posts for this fragment
        val mockPosts = generateMockAllPosts()

        // Initialize and set the adapter with the mock data
        val postAdapter = PostAdapter(mockPosts)
        recyclerView.adapter = postAdapter

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateMockAllPosts(): List<Post> {
        return listOf(
            Post(
                id = UUID.randomUUID(),
                topicName = "Announcements",
                title = "Campus-wide power outage scheduled for this weekend",
                content = null,
                imageUrl = "https://images.unsplash.com/photo-1621252199049-74e7931f6160",
                authorUsername = "TechSupport",
                authorId = UUID.randomUUID(),
                isAnonymous = false,
                upvoteCount = 123,
                downvoteCount = 45,
                commentCount = 67,
                createdAt = LocalDateTime.now().minusHours(2)
            ),
            Post(
                id = UUID.randomUUID(),
                topicName = "Lost & Found",
                title = "Found: A pair of glasses near the library",
                content = "Found a pair of black-rimmed glasses. If they're yours, please contact CampusHelper.",
                imageUrl = "https://images.unsplash.com/photo-1579737119106-a82f3c7b82fe",
                authorUsername = "CampusHelper",
                authorId = UUID.randomUUID(),
                isAnonymous = false,
                upvoteCount = 89,
                downvoteCount = 23,
                commentCount = 45,
                createdAt = LocalDateTime.now().minusHours(3)
            ),
            Post(
                id = UUID.randomUUID(),
                topicName = "Events",
                title = "Movie night at the quad this Friday! 🍿",
                content = "Come join us for a free screening of a popular sci-fi movie. Popcorn will be provided!",
                imageUrl = "https://images.unsplash.com/photo-1540321550546-2495af4453f6",
                authorUsername = "StudentLife",
                authorId = UUID.randomUUID(),
                isAnonymous = false,
                upvoteCount = 101,
                downvoteCount = 34,
                commentCount = 56,
                createdAt = LocalDateTime.now().minusHours(4)
            )
        )
    }
}