package com.example.sonovhost.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.sonovhost.R
import java.io.IOException

class GlideLoader (val context: Context){

    fun loadUserPicture(image: Any, imageView: ImageView){
        try {
            //Load the user image in the ImageView
            Glide
                .with(context)
                .load(image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_placeholder)
                .into(imageView)
        } catch (e: IOException){
            e.printStackTrace()
        }
    }

    fun loadClienHostPicture(image:String, imageView:ImageView){
        try {
            //Load the user image in the ImageView
            Glide .with(context)
                    .load(image)
                    .into(imageView)

        } catch (e: IOException){
            e.printStackTrace()
        }
    }
}