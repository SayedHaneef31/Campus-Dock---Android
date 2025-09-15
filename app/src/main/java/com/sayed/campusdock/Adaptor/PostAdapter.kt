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


class PostAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val ivThumbnail: ImageView = itemView.findViewById(R.id.ivThumbnail)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvSubtitle: TextView = itemView.findViewById(R.id.tvSubtitle)
        val tvUpvoteCount: TextView = itemView.findViewById(R.id.tvUpvoteCount)
        val tvDownvoteCount: TextView = itemView.findViewById(R.id.tvDownvoteCount)
        val tvCommentCount: TextView = itemView.findViewById(R.id.tvCommentCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        // Bind data from the Post model to the views
        holder.tvCategory.text = post.topicName
        holder.tvTitle.text = post.title

        // Handle anonymous posts and display username or "Anonymous"
        holder.tvSubtitle.text = if (post.isAnonymous) {
            "By Anonymous · ${post.createdAt}"
        } else {
            "By @${post.authorUsername} · ${post.createdAt}"
        }

        holder.tvUpvoteCount.text = post.upvoteCount.toString()
        holder.tvDownvoteCount.text = post.downvoteCount.toString()
        holder.tvCommentCount.text = post.commentCount.toString()

        // Handle the thumbnail visibility and image loading
        if (!post.imageUrl.isNullOrEmpty()) {
            holder.ivThumbnail.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(post.imageUrl)
                .into(holder.ivThumbnail)
        } else {
            holder.ivThumbnail.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = posts.size
}