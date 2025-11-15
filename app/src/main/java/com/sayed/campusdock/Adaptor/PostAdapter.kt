package com.sayed.campusdock.Adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sayed.campusdock.Data.Socials.Post
import com.sayed.campusdock.R
import java.util.UUID

class PostAdapter(
    private val posts: List<Post>,
    private val onPostClick: (UUID) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

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

        // --- Click listener ---
        holder.itemView.setOnClickListener {
            onPostClick(post.id)
        }
    }

    override fun getItemCount(): Int = posts.size
}
