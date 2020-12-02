package com.example.myapplication.network

import android.app.Application
import android.provider.ContactsContract
import android.widget.Toast
import androidx.room.Database
import androidx.room.Room
import com.example.myapplication.Post
import com.example.myapplication.adapter.PostAdapter
import com.example.myapplication.room.AppDatabase
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MyApp : Application() {
    companion object {
        lateinit var instance: MyApp
            private set
    }

    var adapter: PostAdapter? = null
    var posts: MutableList<Post> = mutableListOf()
    lateinit var service: PostsInterface
    private lateinit var dataBase: AppDatabase

    private fun fail() {
        Toast.makeText(this@MyApp, "No Internet or bad connection", Toast.LENGTH_LONG).show()
    }

    private fun congratulations(string: String) {
        Toast.makeText(
            this@MyApp,
            string,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        service = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(PostsInterface::class.java)
        dataBase = Room.databaseBuilder(this,
            AppDatabase::class.java,
            "Database")
            .allowMainThreadQueries()
            .build()
    }


    fun load(congratulations: () -> (Unit)) {
        val check = dataBase.postDao().getAllPosts() as MutableList<Post>
        if (check.isEmpty()) {
            val success = fun(response: Response<List<Post>>) {
                response.body()?.forEach { it ->
                    posts.add(it)
                    dataBase.postDao().insertAll(it)
                    adapter?.notifyDataSetChanged()
                }
                congratulations()
            }
            service.getPosts().enqueue(
                function(success)
            )
        } else {
            check.forEach { posts.add(it) }
            adapter?.notifyDataSetChanged()
        }
    }

    fun delete(id: Int) {
        val success = fun(_: Response<ResponseBody>) {
            var index = 1
            for (post in posts) {
                dataBase.postDao().delete(post)
            }
            posts.removeAt(id)
            posts.forEach { it.id = index++ }
            adapter?.notifyDataSetChanged()
            congratulations("Post with id $id was successfully deleted!")
            posts.forEach { dataBase.postDao().insertAll(it) }
        }
        service.deletePost(id).enqueue(function(success))
    }

    fun add(post: Post) {
        val success = fun(_: Response<Post?>) {
            posts.add(post)
            adapter?.notifyDataSetChanged()
            congratulations("Post was downloaded successfully!")
        }
        dataBase.postDao().insertAll(post)
        service.post(post).enqueue(function(success))
    }

    fun restore(congratulations: () -> Unit) {
        for (post in posts) {
            dataBase.postDao().delete(post)
        }
        posts.clear()
        load(congratulations)
    }

    private fun <T> function(success: (Response<T>) -> (Unit)): Callback<T> {
        return object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                success(response)
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                fail()
            }

        }
    }
}