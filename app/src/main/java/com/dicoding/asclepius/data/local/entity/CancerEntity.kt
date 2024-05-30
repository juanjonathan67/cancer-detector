package com.dicoding.asclepius.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "cancers")
data class CancerEntity (
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "imageUri")
    val imageUri: String = "",

    @ColumnInfo(name = "result")
    val result: String = "",
) : Parcelable