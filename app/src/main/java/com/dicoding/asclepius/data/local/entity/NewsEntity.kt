package com.dicoding.asclepius.data.local.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewsEntity (
    val title: String? = "",

    val publisher: String? = "",

    val url: String? = "",

    val imageUrl: String? = "",
) : Parcelable