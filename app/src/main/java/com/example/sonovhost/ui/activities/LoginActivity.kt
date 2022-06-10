package com.example.sonovhost.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.example.sonovhost.R
import com.example.sonovhost.firestore.FirestoreClass
import com.example.sonovhost.models.UserModels
import com.example.sonovhost.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        //Para llenar toda la pantalla --Quita la barra delgada
        @Suppress("DEPRECATION")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        //Click event assigned to forgot password text
        id_tv_forgot_password_login.setOnClickListener(this)
        //Click event assigned to login button
        id_btn_login.setOnClickListener(this)
        //Click event assigned to register text
        id_tv_register_login.setOnClickListener(this)
    }

    //If: al perfil Else: te redirecciona a dashboard
    fun userLoggedInSuccess(user: UserModels){

        //Hide the progress dialog
        hideProgressDialog()

        if (user.profileCompleted == 0){
            //If the user profile is incomplete then launch the userProfileActivity
            val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            //Envia el dato...
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)
        } else {
            //Redirect the user to Main Screen after log in
            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
        }
        finish()
    }

    // In Login sreen the clickable components are login button, forgotPassword text and register text
    override fun onClick(view: View?) {
        if (view != null){
            when(view.id){
                R.id.id_tv_forgot_password_login -> {
                    //Launch the register screen when the user clicks on the text
                    val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }

                R.id.id_btn_login -> {
                    //TODO Step 6: Call the validate function.
                    //START
                    logInRegisteredUser()
                    //END
                }

                R.id.id_tv_register_login -> {
                    //Launch the register screen when the user clicks on the text
                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }


    private fun validateLoginDetails():Boolean{
        return when{
            TextUtils.isEmpty(id_et_email_login.text.toString().trim {it <= ' '} ) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(id_et_password_login.text.toString().trim {it <= ' '} ) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            else -> {
                true
            }
        }
    }

    private fun logInRegisteredUser(){

        if (validateLoginDetails()){

            //Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            //Get the text from editText and trim the space
            val email = id_et_email_login.text.toString().trim { it <= ' ' }
            val password = id_et_password_login.text.toString().trim { it <= ' ' }

            //Log.In using FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful){
                            //TODO - Send user to Main Activity
                            /*    showErrorSnackBar("You are logged in successfully", false) */
                            FirestoreClass().getUserDetails(this@LoginActivity)

                        } else {
                            hideProgressDialog()
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    }
        }
    }

    // Permite que se loguee una sola vez y el apps quede abierto hasta cerrar sesion
    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if(user != null){
            var intent = Intent(this,DashboardActivity::class.java)
            startActivity(intent)
            finish() // Es necesario agregar para que boton onBackPressed permita salir del apps
        }
    }
    /**NOTA: Tuve que activar el codigo en RegisterActivity para que al terminar de registrar te mande al login.
     * Pues si no lo hacia lo botones retroceso me mandama al dashboard sin logearme. */


}