package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MyApp.Companion.instance

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (instance.posts.isEmpty()) {
            instance.load({ recyclerView.adapter?.notifyDataSetChanged() }) {
                Toast.makeText(
                    this@MainActivity,
                    "No connection or bad connection",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        initRecycler()
    }

    private fun initRecycler() {
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = PostAdapter(instance.posts)
    }
}