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
import java.time.format.DateTimeFormatter
import java.util.UUID


class PostAdapter(
    private val posts: List<Post>,
    private val onPostClick: (UUID) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val ivThumbnail: ImageView = itemView.findViewById(R.id.ivThumbnail)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvSubtitle: TextView = itemView.findViewById(R.id.tvSubtitle)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
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

        val formatter = DateTimeFormatter.ofPattern("dd · hh:mm a")

        // Bind data from the Post model to the views
        holder.tvCategory.text = post.topicName
        holder.tvTitle.text = post.title

        // Correctly handle anonymous posts using the isAnonymous field
        holder.tvSubtitle.text = if (post.isAnonymous) {
            "By ${post.authorAnonymousName ?: "Anonymous"}"
        } else {
            "@${post.authorName}"
        }
        holder.tvTime.text = post.createdAt.format(formatter)

        holder.tvUpvoteCount.text = post.upvoteCount.toString()
        holder.tvDownvoteCount.text = post.downvoteCount.toString()
        holder.tvCommentCount.text = post.commentCount.toString()

        // Handle the thumbnail visibility and image loading
        if (!post.imageUrl.isNullOrEmpty()) {
            holder.ivThumbnail.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(post.imageUrl)
                .placeholder(R.drawable.student_union) // 👈 Add this line
                .into(holder.ivThumbnail)

        } else {
            holder.ivThumbnail.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onPostClick(post.id)
        }
    }

    override fun getItemCount(): Int = posts.size
}