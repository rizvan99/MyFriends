package com.example.friends_app.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.friends_app.Model.BEFriend
import java.util.concurrent.Executors

class FriendRepository private constructor(private val context: Context) {
    private val database: FriendDb = Room.databaseBuilder(
        context.applicationContext,
        FriendDb::class.java,
        "friend-database")
        .build()

    private val friendDao = database.friendDao()

    fun getAll(): LiveData<List<BEFriend>> = friendDao.getAll()

    fun getById(id: Int) = friendDao.getById(id)

    private val executor = Executors.newSingleThreadExecutor()

    fun insert(f: BEFriend) {
        executor.execute {friendDao.insert(f)}
    }

    fun update(f: BEFriend) {
        executor.execute {friendDao.update(f)}
    }

    fun delete(f: BEFriend) {
        executor.execute {friendDao.delete(f)}
    }

    companion object {
        private var Instance: FriendRepository? = null

        fun initialize(context: Context) {
            if (Instance == null)
                Instance = FriendRepository(context)
        }

        fun get(): FriendRepository {
            if (Instance != null) return Instance!!
            throw IllegalStateException("Friend repo not initialized")
        }
    }


}