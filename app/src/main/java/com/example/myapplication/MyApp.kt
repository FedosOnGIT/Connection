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

    var posts: MutableList<Post> = mutableListOf()
    lateinit var service: PostsInterface

    override fun onCreate() {
        super.onCreate()
        instance = this
        service = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(PostsInterface::class.java)
    }

    fun load(change: () -> (Unit), fail: (Throwable) -> (Unit)) {
        service.getPosts().enqueue(object : Callback<List<Post?>?> {
            override fun onResponse(
                call: Call<List<Post?>?>,
                response: Response<List<Post?>?>
            ) {
                response.body()?.forEach { it ->
                    if (it != null) {
                        posts.add(it)
                        change()
                    }
                }
            }

            override fun onFailure(call: Call<List<Post?>?>, t: Throwable) {
                fail(t)
            }
        })
    }

    fun delete(id : Int, change: () -> (Unit)) {
        service.deletePost(id).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                posts.removeAt(id)
                var index = 1
                posts.forEach { it -> it.id = index++ }
                change()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(
                    this@MyApp,
                    "No connection or bad connection",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }




}