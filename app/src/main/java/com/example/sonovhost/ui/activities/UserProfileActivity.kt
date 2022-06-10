package com.example.sonovhost.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sonovhost.R
import com.example.sonovhost.firestore.FirestoreClass
import com.example.sonovhost.models.UserModels
import com.example.sonovhost.utils.Constants
import com.example.sonovhost.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mUserDetails:UserModels
    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageURL:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            // Get the user details from intent as a parcelableExtra
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        //Toma los datos de firebase
        id_et_firts_name_profile.setText(mUserDetails.firstName)
        id_et_last_name_profile.setText(mUserDetails.lastName)
        id_et_email_profile.isEnabled = false /** No se puede editar, solo se muestra el EditText */
        id_et_email_profile.setText(mUserDetails.email)

        //COMPLETE PROFILE -Primera Vez
        if (mUserDetails.profileCompleted == 0){
            id_tv_title_profile.text = resources.getString(R.string.title_complete_profile) /** solo cumple cuando te logeas por primera vez */
            id_et_firts_name_profile.isEnabled = false /** No se puede editar solo se muestra el EditText  */
            id_et_last_name_profile.isEnabled = false /** No se puede editar,  solo se muestra el EditText */

        }else{
            // Cambia el texto a EDIT PROFILE en el toolbar
            setupActionBar()
            id_tv_title_profile.text = resources.getString(R.string.title_edit_profile) /** Se activa al ingresar en configur y clip en Edit*/
            GlideLoader(this@UserProfileActivity).loadUserPicture(mUserDetails.image, id_iv_user_photo_profile)

            if (mUserDetails.mobile != 0L){
                id_et_mobile_number_profile.setText(mUserDetails.mobile.toString())
            }

            //El radioButtom cambia segun activacion o desactivacion del firebase - Es automatico
            if (mUserDetails.gender == Constants.MALE){
                id_rb_male_profile.isChecked = true
            }else{
                id_rb_female_profile.isChecked = true
            }
        }

        id_iv_user_photo_profile.setOnClickListener(this@UserProfileActivity)
        id_btn_submit_profile.setOnClickListener(this@UserProfileActivity)
    }


    //Icono retroceder
    private fun setupActionBar(){
        setSupportActionBar(toolbar_user_profile_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbar_user_profile_activity.setNavigationOnClickListener { onBackPressed() }
    }


    //Carga Imagen y Ejecutar id_btn_submit_profile
    override fun onClick(view: View?) {
        if (view != null){
            when(view.id){

                R.id.id_iv_user_photo_profile -> {
                    /* Here we will if the permission is already allowed or we need to request for it.
                      first of all we will check the Read_external_storage permission and if it is allowed we */
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ){
                        //         showErrorSnackBar("You already have the storage permission.", false)
                        Constants.showImageChooser(this)

                    } else{
                        /*  Requests permissions to be granted to this application. These permissions
                         must be request in your manifest, they should not be granted to your app,
                         and they should have protecction level*/

                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.id_btn_submit_profile -> {
                    // if numer telefono
                    if (validateUserProfileDetails()){
                        showProgressDialog(resources.getString(R.string.please_wait))

                        // if imagen
                        if (mSelectedImageFileUri != null)
                            FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri)
                        else{
                            updateUserProfileDetails()
                        }
                    }
                }
            }
        }
    }

    private fun updateUserProfileDetails(){

        val userHashMap = HashMap<String, Any>()

        val firstName = id_et_firts_name_profile.text.toString().trim { it <= ' ' }
             if (firstName != mUserDetails.firstName){
            userHashMap[Constants.FIRST_NAME] = firstName
            }

        val lastName = id_et_last_name_profile.text.toString().trim { it <= ' ' }
             if (lastName != mUserDetails.lastName){
            userHashMap[Constants.LAST_NAME] = lastName
            }

        val mobileNumber = id_et_mobile_number_profile.text.toString().trim { it <= ' ' }

        val gender =
            if (id_rb_male_profile.isChecked){
            Constants.MALE
            } else {
            Constants.FEMALE
            }

        if (mUserProfileImageURL.isNotEmpty()){
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }

        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        if (gender.isNotEmpty() && gender != mUserDetails.gender){
            userHashMap[Constants.GENDER] = gender
        }

        userHashMap[Constants.GENDER] = gender

        //Conteo de completar perfil
        userHashMap[Constants.COMPLETE_PROFILE] = 1

        //      showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().updateUserProfileData(this, userHashMap)

        //    showErrorSnackBar("Your details are valid. You can update them", false)
    }


    fun userProfileUpdateSuccess(){
        hideProgressDialog()

        Toast.makeText(
            this@UserProfileActivity,
            resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_SHORT
        ).show()

        startActivity(Intent(this@UserProfileActivity, DashboardActivity::class.java))
        finish()
    }


    //Permiso a camara
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //     showErrorSnackBar("The storage permission is granted.", false)
                Constants.showImageChooser(this)
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    //Seleccion Imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE){
                if (data != null){
                    try {
                        //The uri of selected image from phone storage.
                        mSelectedImageFileUri = data.data!!

                        // iv_user_photo.setImageURI(selectedImageFileUri)
                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, id_iv_user_photo_profile)

                    } catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(
                            this@UserProfileActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } else if(resultCode == Activity.RESULT_CANCELED){
            // A log is printed when user close or cancel the image selection
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }


    //Validar numero telefono
    private fun validateUserProfileDetails(): Boolean{
        return when{
            TextUtils.isEmpty(id_et_mobile_number_profile.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number), true)
                false
            }
            else ->{
                true
            }
        }
    }

    //Se carga el url en el firestore
    fun imageUploadSuccess(imageURL:String){
        //    hideProgressDialog()
        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
    }

}