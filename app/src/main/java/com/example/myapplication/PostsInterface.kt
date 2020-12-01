package com.example.myapplication

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface PostsInterface {
    @GET("posts")
    fun getPosts(): Call<List<Post>>

    @DELETE("posts/{number}")
    fun deletePost(@Path("number") number: Int): Call<ResponseBody>

    @POST("posts")
    fun post(@Body post: Post): Call<Post?>
}