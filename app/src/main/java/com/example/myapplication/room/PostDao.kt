package com.example.myapplication.room

import androidx.room.*
import com.example.myapplication.Post

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg post: Post)

    @Delete
    suspend fun delete(vararg post: Post)

    @Query("SELECT * FROM post")
    suspend fun getAllPosts() : List<Post>?

    @Query("DELETE FROM post")
    suspend fun deleteAll()
}