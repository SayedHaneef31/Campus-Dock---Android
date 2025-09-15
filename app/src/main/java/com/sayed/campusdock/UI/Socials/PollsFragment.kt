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

class PollsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_polls, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewAllPosts) // Use the same RecyclerView ID
        recyclerView.layoutManager = LinearLayoutManager(context)

        val mockPosts = generateMockPolls()
        val postAdapter = PostAdapter(mockPosts)
        recyclerView.adapter = postAdapter

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateMockPolls(): List<Post> {
        return listOf(
            Post(
                id = UUID.randomUUID(),
                topicName = "Poll",
                title = "I think college should have a student union!!",
                content = "A student union could represent student interests and improve campus life. What do you think?",
                imageUrl = "https://images.unsplash.com/photo-1549480833-281b37d80540",
                authorUsername = "ChangeMaker",
                authorId = UUID.randomUUID(),
                isAnonymous = true,
                upvoteCount = 85,
                downvoteCount = 10,
                commentCount = 42,
                createdAt = LocalDateTime.now().minusDays(1)
            ),
            Post(
                id = UUID.randomUUID(),
                topicName = "Poll",
                title = "Should the university ban single-use plastic bottles?",
                content = "A proposal to ban plastic bottles is being considered to promote sustainability. Vote and comment on your stance.",
                imageUrl = "https://images.unsplash.com/photo-1525287313837-1755a1226154",
                authorUsername = "EcoWarrior",
                authorId = UUID.randomUUID(),
                isAnonymous = false,
                upvoteCount = 150,
                downvoteCount = 30,
                commentCount = 60,
                createdAt = LocalDateTime.now().minusHours(8)
            )
        )
    }
}