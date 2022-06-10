package com.example.sonovhost.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    const val USERS: String = "users"
    const val MYSHOPPAL_PREFERENCES: String = "MyShopPalPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"
    const val EXTRA_USER_DETAILS:String = "extra_user_details"
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1

    const val MALE:String = "male"
    const val FEMALE:String = "female"
    const val FIRST_NAME:String = "firstName"
    const val LAST_NAME:String = "lastName"
    const val MOBILE:String = "mobile"
    const val GENDER:String = "gender"
    const val IMAGE:String = "image"
    const val COMPLETE_PROFILE:String = "profileCompleted"

    const val USER_PROFILE_IMAGE:String = "User_Profile_Image"


    //Esto es del ClienHostAddActivity
    const val ENVIADO:String = "Msn Enviado"
    const val NO_ENVIADO:String = "Msn NO Enviado"
    const val SI_RENOVADO_HOST:String = "Si Renovado Host"
    const val NO_RENOVADO_HOST:String = "No Renovado Host"



   //Muestra el selector de Imagen
    fun showImageChooser(activity: Activity){
        //An intent four launching the image selection of phone storage
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        //Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }


    fun getFileExtension(activity: Activity, uri: Uri?): String?{
        /*
        MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
        GetSingleton(): Get the singleton instance of MimeTypeMap
        getExtensionFromMimeType: Return the registered extension for the given MIME type
        ContentResolver.getType:Return the MIME type of the given content URL
         */

        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}