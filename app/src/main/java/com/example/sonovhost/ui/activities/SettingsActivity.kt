package com.example.sonovhost.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.sonovhost.R
import com.example.sonovhost.firestore.FirestoreClass
import com.example.sonovhost.models.UserModels
import com.example.sonovhost.utils.Constants
import com.example.sonovhost.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mUserDetails:UserModels

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupActionBar()

        id_tv_edit_settings.setOnClickListener(this)
        id_btn_logout_settings.setOnClickListener(this)

    }

    private fun setupActionBar(){

        setSupportActionBar(id_toolbar_settings_activity)

        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        id_toolbar_settings_activity.setNavigationOnClickListener { onBackPressed() }
    }

    //Obtiene datos de firebase, carga y pasa onResume
    private fun getUserDetails(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserDetails(this)
    }

    //Muestra los datos
    fun userDetailsSuccess(user: UserModels){
        mUserDetails = user

        hideProgressDialog()

        GlideLoader(this@SettingsActivity).loadUserPicture(user.image, iv_user_photo)
        id_tv_name_settings.text = "${user.firstName} ${user.lastName}"
        id_tv_gender_settings.text = user.gender
        id_tv_email_settings.text = user.email
        id_tv_mobile_number_settings.text = "${user.mobile}"
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }

    override fun onClick(v: View?) {
        if (v != null){
            when(v.id){

                R.id.id_tv_edit_settings ->{
                    val intent = Intent(this@SettingsActivity, UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
                    startActivity(intent)
                }

                R.id.id_btn_logout_settings ->{
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}