package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MyApp.Companion.instance
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRecycler()
        if (instance.posts.isEmpty()) {
            instance.adapter = recyclerView.adapter as PostAdapter?
            instance.load() {
                Toast.makeText(
                    this@MainActivity,
                    "All posts have been uploaded",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        button.setOnClickListener {
            val window = Intent(this, CreatePost::class.java)
            startActivity(window)
        }
    }

    private fun initRecycler() {
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = PostAdapter(instance.posts)
    }
}