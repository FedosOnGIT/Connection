package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.MyApp.Companion.instance
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.activity_main.*

class CreatePost : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        create.setOnClickListener {
            var userId = 1
            instance.posts.forEach { if (it.userId > userId) userId = it.userId }
            val post = Post(
                instance.posts.size,
                EnterTitle.text.toString(),
                EnterPost.text.toString(),
                userId + 1
            )
            instance.add(post)
            finish()
        }
    }
}