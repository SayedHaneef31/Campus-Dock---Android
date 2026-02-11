package com.sayed.campusdock.Adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sayed.campusdock.Data.Socials.Post
import com.sayed.campusdock.Data.Socials.VoteType
import com.sayed.campusdock.R
import java.util.UUID

class PostAdapter(
    private var posts: List<Post>,
    private val onPostClick: (UUID) -> Unit,
    private val onVote: (postId: UUID, userId: UUID, voteType: VoteType) -> Unit = { _, _, _ -> }
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    // Method to update posts without recreating the adapter
    fun updatePosts(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged()
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivUserProfile: ImageView = itemView.findViewById(R.id.ivUserProfile)
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsernameTime)
        val tvTopicName: TextView = itemView.findViewById(R.id.tvTopicName)
//        val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        val ivPostImage: ImageView = itemView.findViewById(R.id.ivPostImage)

        val ivUpvote: ImageView = itemView.findViewById(R.id.ivUpvote)
        val tvUpvoteCount: TextView = itemView.findViewById(R.id.tvUpvoteCount)
        val ivDownvote: ImageView = itemView.findViewById(R.id.ivDownvote)
        val tvDownvoteCount: TextView = itemView.findViewById(R.id.tvDownvoteCount)
        val ivComment: ImageView = itemView.findViewById(R.id.ivComment)
        val tvCommentCount: TextView = itemView.findViewById(R.id.tvCommentCount)

        // Track current vote state for this post
        var currentVoteState: VoteType? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.socials_item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        val context = holder.itemView.context

        // --- User Info ---
        holder.tvTopicName.text = "r/${post.topicName ?: "General"}"
        holder.tvUsername.text = if (post.isAnonymous) {
            post.authorAnonymousName ?: "Anonymous"
        } else {
            "@${post.authorName}"
        }

        // --- Post title ---
        holder.itemView.findViewById<TextView>(R.id.tvPostTitle).text = post.title

        // --- Optional Image ---
        if (!post.imageUrl.isNullOrEmpty()) {
            holder.ivPostImage.visibility = View.VISIBLE
            Glide.with(context)
                .load(post.imageUrl)
                .placeholder(R.drawable.banner4) // use any placeholder
                .into(holder.ivPostImage)
        } else {
            holder.ivPostImage.visibility = View.GONE
        }

        // --- Profile Picture ---
//        if (!post.authorProfileUrl.isNullOrEmpty() && !post.isAnonymous) {
//            Glide.with(context)
//                .load(post.authorProfileUrl)
//                .circleCrop()
//                .into(holder.ivUserProfile)
//        } else {
//            holder.ivUserProfile.setImageResource(R.drawable.profile_icon)
//        }
        holder.ivUserProfile.setImageResource(R.drawable.profile_icon)

        // --- Votes & Comments ---
        holder.tvUpvoteCount.text = post.upvoteCount.toString()
        holder.tvDownvoteCount.text = post.downvoteCount.toString()
        holder.tvCommentCount.text = post.commentCount.toString()

        // --- Upvote Click Listener ---
        holder.ivUpvote.setOnClickListener {
            val userId = UUID.fromString("00000000-0000-0000-0000-000000000000") // This will be set from fragment
            if (holder.currentVoteState == VoteType.UPVOTE) {
                // Already upvoted, toggle off
                holder.currentVoteState = null
                updateVoteUI(holder, null)
                // Update count optimistically
                val currentCount = holder.tvUpvoteCount.text.toString().toIntOrNull() ?: 0
                holder.tvUpvoteCount.text = (currentCount - 1).toString()
            } else {
                // Upvote
                val wasDownvoting = holder.currentVoteState == VoteType.DOWNVOTE
                holder.currentVoteState = VoteType.UPVOTE
                updateVoteUI(holder, VoteType.UPVOTE)
                // Update counts optimistically
                val upvoteCount = holder.tvUpvoteCount.text.toString().toIntOrNull() ?: 0
                holder.tvUpvoteCount.text = (upvoteCount + 1).toString()
                if (wasDownvoting) {
                    val downvoteCount = holder.tvDownvoteCount.text.toString().toIntOrNull() ?: 0
                    holder.tvDownvoteCount.text = (downvoteCount - 1).toString()
                }
            }
            onVote(post.id, userId, VoteType.UPVOTE)
        }

        // --- Downvote Click Listener ---
        holder.ivDownvote.setOnClickListener {
            val userId = UUID.fromString("00000000-0000-0000-0000-000000000000") // This will be set from fragment
            if (holder.currentVoteState == VoteType.DOWNVOTE) {
                // Already downvoted, toggle off
                holder.currentVoteState = null
                updateVoteUI(holder, null)
                // Update count optimistically
                val currentCount = holder.tvDownvoteCount.text.toString().toIntOrNull() ?: 0
                holder.tvDownvoteCount.text = (currentCount - 1).toString()
            } else {
                // Downvote
                val wasUpvoting = holder.currentVoteState == VoteType.UPVOTE
                holder.currentVoteState = VoteType.DOWNVOTE
                updateVoteUI(holder, VoteType.DOWNVOTE)
                // Update counts optimistically
                val downvoteCount = holder.tvDownvoteCount.text.toString().toIntOrNull() ?: 0
                holder.tvDownvoteCount.text = (downvoteCount + 1).toString()
                if (wasUpvoting) {
                    val upvoteCount = holder.tvUpvoteCount.text.toString().toIntOrNull() ?: 0
                    holder.tvUpvoteCount.text = (upvoteCount - 1).toString()
                }
            }
            onVote(post.id, userId, VoteType.DOWNVOTE)
        }

        // --- Click listener ---
        holder.itemView.setOnClickListener {
            onPostClick(post.id)
        }
    }

    private fun updateVoteUI(holder: PostViewHolder, voteType: VoteType?) {
        val context = holder.itemView.context
        
        when (voteType) {
            VoteType.UPVOTE -> {
                holder.ivUpvote.setColorFilter(
                    ContextCompat.getColor(context, R.color.green_dark),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                holder.ivDownvote.setColorFilter(
                    ContextCompat.getColor(context, R.color.red),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
            VoteType.DOWNVOTE -> {
                holder.ivDownvote.setColorFilter(
                    ContextCompat.getColor(context, R.color.red),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                holder.ivUpvote.setColorFilter(
                    ContextCompat.getColor(context, R.color.green_dark),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
            null -> {
                holder.ivUpvote.clearColorFilter()
                holder.ivDownvote.clearColorFilter()
            }
        }
    }

    override fun getItemCount(): Int = posts.size
}
