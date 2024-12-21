package com.project.dicodingevent.data.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "favorite_event")
data class FavoriteEvent(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: String = "",

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "description")
    var description: String = "",

    @ColumnInfo(name = "link")
    var link: String = "",

    @ColumnInfo(name = "owner_name")
    var ownerName: String = "",

    @ColumnInfo(name = "city_name")
    var cityName: String = "",

    @ColumnInfo(name = "begin_time")
    var beginTime: String = "",

    @ColumnInfo(name = "media_cover")
    var mediaCover: String? = null,

    @ColumnInfo(name = "image_logo")
    var imageLogo: String? = null
) : Parcelable
