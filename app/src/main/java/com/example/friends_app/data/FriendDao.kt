package com.example.friends_app.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.friends_app.Model.BEFriend

@Dao
interface FriendDao {
    @Query("SELECT * from BEFriend")
    fun getAll(): LiveData<List<BEFriend>>

    @Query("SELECT * from BEFriend where id = (:id)")
    fun getById(id: Int): LiveData<BEFriend>

    @Insert
    fun insert(f: BEFriend)

    @Update
    fun update(f: BEFriend)

    @Delete
    fun delete(f: BEFriend)

}