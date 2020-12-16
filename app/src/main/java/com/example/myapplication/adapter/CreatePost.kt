package com.example.myapplication.adapter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.R
import com.example.myapplication.network.MyApp.Companion.instance
import com.example.myapplication.Post
import kotlinx.android.synthetic.main.activity_create_post.*

class CreatePost : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        create.setOnClickListener {
            var userId = 1
            instance.posts.forEach { if (it.userId > userId) userId = it.userId }
            val post = Post(
                instance.posts.last().id + 1,
                EnterTitle.text.toString(),
                EnterPost.text.toString(),
                userId + 1
            )
            instance.add(post)
            finish()
        }
    }
}