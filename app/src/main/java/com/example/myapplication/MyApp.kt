package com.example.myapplication

import android.app.Application
import android.widget.Toast
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
    }


    fun load(congratulations: () -> (Unit)) {
        val success = fun(response: Response<List<Post>>) {
            response.body()?.forEach { it ->
                posts.add(it)
                adapter?.notifyDataSetChanged()
            }
            congratulations()
        }
        service.getPosts().enqueue(
            function(success)
        )
    }

    fun delete(id: Int) {
        val success = fun(_: Response<ResponseBody>) {
            posts.removeAt(id)
            var index = 1
            posts.forEach { it -> it.id = index++ }
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
        service.post(post).enqueue(function<Post?>(success))
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