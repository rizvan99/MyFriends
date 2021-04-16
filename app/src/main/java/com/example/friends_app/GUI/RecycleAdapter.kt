package com.example.friends_app.GUI

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.friends_app.Model.BEFriend
import com.example.friends_app.R

class RecycleAdapter(val listener: MainActivity) : RecyclerView.Adapter<RecycleAdapter.FriendViewHolder>() {
    class FriendViewHolder(itemView: View, val listener: MainActivity) : RecyclerView.ViewHolder(itemView) {
        val txtName = itemView.findViewById(R.id.tvName) as TextView
        val txtPhone = itemView.findViewById(R.id.tvPhone) as TextView
        val imgFavorite = itemView.findViewById(R.id.imgBtnIsFav) as ImageView
        val imgPicture = itemView.findViewById(R.id.imgPicture) as ImageView
    }

    private var friends = ArrayList<BEFriend>()

    fun setListData(data: ArrayList<BEFriend>) {
        this.friends = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.cell, parent, false)
        return FriendViewHolder(itemView, listener)
    }

    override fun getItemCount(): Int {
        return friends.size
    }


    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val currentItem = friends[position]

        holder.txtName.text = currentItem.name.toString()
        holder.txtPhone.text = currentItem.phone.toString()
        holder.imgFavorite.setImageResource(if (currentItem.isFavorite) R.drawable.ok else R.drawable.notok)
        holder.imgPicture.setImageURI(Uri.parse(currentItem.imagePath))

        holder.itemView.setOnClickListener {
            listener.onItemClickListener(friends[position])
        }
    }

    interface RowClickListener{
        fun onItemClickListener(friend: BEFriend)
    }


}