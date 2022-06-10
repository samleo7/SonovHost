package com.example.sonovhost.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.example.sonovhost.ui.activities.LoginActivity
import com.example.sonovhost.ui.activities.RegisterActivity
import com.example.sonovhost.ui.activities.UserProfileActivity
import com.example.sonovhost.models.UserModels
import com.example.sonovhost.ui.activities.SettingsActivity
import com.example.sonovhost.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    //Registra los datos de usuario en firebase
    fun registerUser(activity: RegisterActivity, userInfo: UserModels){
        //The users is collection name. If the collection is already created then it will not create the same one again
        mFireStore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                //Enlazado:RegisterActivity
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                //Enlazado:BaseActivity
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user",
                    e
                )
            }
    }

    fun getCurrentUserID():String{
        //An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        //A variable to assign the currentUserId if it is not null or else it will be blank
        var currentUserID = ""
        if (currentUser != null ){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getUserDetails(activity: Activity){

        //Here we pass the collection name from which we wants the data
        mFireStore.collection(Constants.USERS)
            //The document id to get the fields of user
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                Log.i(activity.javaClass.simpleName, document.toString())

                //Here we have received the document snapshot wich is converted into the user data model object
                val user = document.toObject(UserModels::class.java)!!

                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constants.MYSHOPPAL_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                val editor:SharedPreferences.Editor = sharedPreferences.edit()
                //Key:logged_in_username Value:sam leo
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()

                //START
                //TODO step 6: pass the result to the login activity
                when(activity){
                    is LoginActivity -> {
                        //Call a function of base activity for transferring the result to it
                        activity.userLoggedInSuccess(user)
                    }
                    //Toma datos de fire y se envia al settings
                    is SettingsActivity ->{
                        activity.userDetailsSuccess(user)
                    }
                }
                //END
            }
            .addOnFailureListener { e ->
                //Hide the progress dialog if there is any error. And print the error in log
                when(activity){
                    is LoginActivity ->{
                        activity.hideProgressDialog()
                    }
                    is SettingsActivity ->{
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details",
                    e
                )
            }
    }


    fun updateUserProfileData(activity: Activity, userHashMap:HashMap<String, Any>){

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when(activity){
                    is UserProfileActivity -> {
                        //Hide the progress dialog if there is any error. And print the error in log.
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when(activity){
                    is UserProfileActivity -> {
                        //Hide the progress dialog if there is any error. And print the error in log.
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details",
                    e
                )
            }
    }


    fun uploadImageToCloudStorage(activity: Activity, imageFileUri: Uri?){
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(
                activity,
                imageFileUri
            )
        )

        sRef.putFile(imageFileUri!!).addOnSuccessListener { taskSnapshot ->
            //The image upload is success
            Log.e(
                "Firebase Image URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )

            //Get the downloadable url from the task snapshot
            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                    Log.e("Downloadable Image URL", uri.toString())
                    when(activity){
                        is UserProfileActivity ->{
                            activity.imageUploadSuccess(uri.toString())
                        }
                    }
                }
        }

            .addOnFailureListener { exception ->

                //Hide the progress dialog if there is any error. And print the error in log
                when(activity){
                    is UserProfileActivity ->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }


}