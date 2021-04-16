package com.example.friends_app.GUI

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.friends_app.Model.BEFriend
import com.example.friends_app.R
import com.example.friends_app.data.FriendRepository
import com.google.android.gms.maps.model.LatLng


class MainActivity : AppCompatActivity(), RecycleAdapter.RowClickListener {

    private val RESULT_CODE_CREATE = 1
    private val RESULT_CODE_DELETE = 3
    private val RESULT_CODE_UPDATE = 4

    lateinit var friendAdapter: RecycleAdapter
    private lateinit var clickedFriend: BEFriend

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FriendRepository.initialize(this)
        friendAdapter = RecycleAdapter(this@MainActivity)

        val recycler = findViewById<RecyclerView>(R.id.recyclerView)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.addItemDecoration(
                DividerItemDecoration(
                        this,
                        DividerItemDecoration.VERTICAL
                )
        )

        recycler.adapter = friendAdapter

        val mRep = FriendRepository.get()

        mRep.getAll().observe(this, Observer {
            friendAdapter.setListData(ArrayList(it))
            friendAdapter.notifyDataSetChanged()
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intentCreate = Intent(this, DetailActivity::class.java)
        val intentMap = Intent(this@MainActivity, MapsActivity::class.java)
        val mRep = FriendRepository.get()
        when (item.toString()) {
            "Add friend" -> {
                intentCreate.putExtra("isCreate", true)
                startActivityForResult(intentCreate, 1)
            }
            "Open maps" -> {
                intentMap.putExtra("newMarker", false)
                startActivityForResult(intentMap, 200)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_CODE_CREATE) {
            val newFriend = data?.extras?.getSerializable("newFriend") as BEFriend
            val mRep = FriendRepository.get()
            mRep.insert(newFriend)
        }

        if (resultCode == RESULT_CODE_UPDATE) {
            Log.e("xyz","Updating ...")
            val updateFriend = data?.extras?.getSerializable("updatedFriend") as BEFriend
            val mRep = FriendRepository.get()
            mRep.update(updateFriend)
        }

        if(resultCode == RESULT_CODE_DELETE)
        {
            val friendToDelete = data?.extras?.getSerializable("friendToDelete") as BEFriend
            val mRep = FriendRepository.get()
            mRep.delete(friendToDelete)
            friendAdapter.notifyDataSetChanged()
        }
    }

    override fun onItemClickListener(friend: BEFriend) {
        Log.e("xyz", "You clicked " + friend.name + friend.id + friend.imagePath)
        clickedFriend = friend
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("friend", friend)
        intent.putExtra("isCreate", false)
        startActivityForResult(intent, 2)
    }

}