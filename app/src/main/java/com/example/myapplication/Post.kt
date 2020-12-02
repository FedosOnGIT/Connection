package com.example.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Post(
    @PrimaryKey var id : Int,
    val title : String,
    val body : String,
    val userId : Int
)