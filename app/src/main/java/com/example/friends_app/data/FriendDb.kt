package com.example.friends_app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.friends_app.Model.BEFriend

@Database(entities = [BEFriend::class], version=1)
abstract class FriendDb : RoomDatabase() {


    abstract fun friendDao(): FriendDao
}