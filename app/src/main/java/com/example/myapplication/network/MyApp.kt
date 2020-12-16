package com.example.myapplication.network

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
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
import kotlinx.coroutines.*

class MyApp : Application() {
    companion object {
        lateinit var instance: MyApp
            private set
        const val EMPTY = "true"
    }

    var adapter: PostAdapter? = null
    var posts: MutableList<Post> = mutableListOf()
    lateinit var service: PostsInterface
    private lateinit var dataBase: AppDatabase
    private var sharedPreference: SharedPreferences? = null
    var emptyBase: Boolean = true

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
        sharedPreference = getSharedPreferences("take", Context.MODE_PRIVATE)
        emptyBase = sharedPreference?.getBoolean(EMPTY, true) ?: true
        service = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(PostsInterface::class.java)
        dataBase = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "Database"
        )
            .build()
    }

    // shared preference
    fun load() {
        if (emptyBase) {
            val success = fun(response: Response<List<Post>>) {
                response.body()?.forEach { it ->
                    posts.add(it)
                    adapter?.notifyDataSetChanged()
                    Log.i("postsSize", posts.size.toString())
                }
                CoroutineScope(Dispatchers.IO).launch {
                    dataBase.postDao().insertAll(*posts.toTypedArray())
                    emptyBase = false
                    sharedPreference?.edit()?.apply {
                        this.putBoolean(EMPTY, emptyBase)
                        apply()
                    }
                }
                congratulations("All posts have been uploaded")
            }
            service.getPosts().enqueue(
                function(success)
            )
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                val data = dataBase.postDao().getAllPosts() as MutableList<Post>
                data.forEach { posts.add(it) }
                withContext(Dispatchers.Main) {
                    adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    fun delete(id: Int) {
        val success = fun(_: Response<ResponseBody>) {
            var index = 0
            for (post in posts) {
                if (posts[index].id == id) {
                    break
                }
                index++
            }
            CoroutineScope(Dispatchers.IO).launch {
                dataBase.postDao().delete(posts[index])
            }
            Log.i("Coroutines worked", index.toString())
            posts.removeAt(index)
            adapter?.notifyDataSetChanged()
            congratulations("Post with id $id was successfully deleted!")
        }
        service.deletePost(id).enqueue(function(success))
    }

    fun add(post: Post) {
        val success = fun(_: Response<Post?>) {
            posts.add(post)
            adapter?.notifyDataSetChanged()
            congratulations("Post was downloaded successfully!")
        }
        service.post(post).enqueue(function(success))
        CoroutineScope(Dispatchers.IO).launch {
            dataBase.postDao().insertAll(post)
        }
    }

    fun restore(congratulations: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            dataBase.postDao().deleteAll()
            withContext(Dispatchers.Main) {
                posts.clear()
                emptyBase = true
                load()
            }
        }
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

