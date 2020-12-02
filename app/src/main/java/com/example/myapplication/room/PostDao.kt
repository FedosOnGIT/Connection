package com.example.myapplication.room

import androidx.room.*
import com.example.myapplication.Post

@Dao
interface PostDao {

    @Insert
    fun insertAll(vararg post: Post)

    @Delete
    fun delete(vararg post: Post)

    @Query("SELECT * FROM post")
    fun getAllPosts() : List<Post>?
}