package com.example.sonovhost.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
class UserModels (
    val id:String = "",
    val firstName:String = "",
    val lastName:String = "",
    val email:String = "",
    val image:String = "",
    val mobile:Long = 0,
    val gender:String = "",
    val profileCompleted:Int = 0
): Parcelable