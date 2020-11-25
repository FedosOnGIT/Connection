package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.post.view.*

class PostAdapter(
    private val posts: List<Post>
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    inner class PostViewHolder(val root : View) : RecyclerView.ViewHolder(root) {
        fun bind(post: Post) {
            with(root) {
                title.text = post.title
                body.text = post.body
                deletePost.setOnClickListener {
                    MyApp.instance.delete(post.id - 1) { this@PostAdapter.notifyDataSetChanged() }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.post, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size
}