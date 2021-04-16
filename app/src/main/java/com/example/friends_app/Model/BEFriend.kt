package com.example.friends_app.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable
import java.util.*

@Entity
data class BEFriend(
        @PrimaryKey(autoGenerate = true)
        var id: Int,
        var name: String,
        var phone: String,
        var address: String,
        var isFavorite: Boolean,
        var email: String,
        var website: String,
        var birthday: String,
        var latitude: Double,
        var longitude: Double,
        var imagePath: String?) : Serializable