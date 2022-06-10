package com.example.sonovhost.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.example.sonovhost.R
import com.example.sonovhost.firestore.FirestoreClass
import com.example.sonovhost.models.UserModels
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

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

        id_tv_login_register.setOnClickListener {
        /*    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)  */
            onBackPressed()
        }

        id_btn_register.setOnClickListener {
            registerUser()
        }

        setupActionBar()
    }

    //Flecha para retroceder
    private fun setupActionBar(){
        setSupportActionBar(id_toolbar_register_activity)

        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_black_color)
        }
        id_toolbar_register_activity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * A function to validate the entries of a new user
     */
    private fun validateRegisterDetails():Boolean{

        return when{
            TextUtils.isEmpty(id_et_first_name_register.text.toString().trim {it <= ' '} ) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }

            TextUtils.isEmpty(id_et_last_name_register.text.toString().trim {it <= ' '} ) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }

            TextUtils.isEmpty(id_et_email_register.text.toString().trim {it <= ' '} ) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(id_et_password_register.text.toString().trim {it <= ' '} ) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            TextUtils.isEmpty(id_et_confirm_password_register.text.toString().trim {it <= ' '} ) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_confirm_password), true)
                false
            }

            id_et_password_register.text.toString().trim { it <= ' ' } != id_et_confirm_password_register.text.toString()
                    .trim{it <= ' '} -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password_and_confirm_password_mismatch), true)
                false
            }
            !id_cb_terms_and_condition_register.isChecked -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_agree_terms_and_condition), true)
                false
            }
            else -> {
            //      showErrorSnackBar(resources.getString(R.string.registery_successfull), false)
                true
            }
        }
    }

    private fun registerUser(){

        //Check with validate function if the entries are valid or not
        if(validateRegisterDetails()) {

            //Muestra Progress
            showProgressDialog(resources.getString(R.string.please_wait))

            val email: String = id_et_email_register.text.toString().trim { it <= ' ' }
            val password:String = id_et_password_register.text.toString().trim { it <= ' ' }

            //Create an instance and create a register a user with email and password
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(
                            OnCompleteListener<AuthResult> { task ->

                                //If the registration is successfully done
                                if (task.isSuccessful){
                                    //Firebase registered user
                                    val firebaseUser: FirebaseUser = task.result!!.user!!

                                    //Guardando el models
                                    val user = UserModels(
                                        firebaseUser.uid,
                                        id_et_first_name_register.text.toString().trim { it <= ' ' },
                                        id_et_last_name_register.text.toString().trim { it <= ' ' },
                                        id_et_email_register.text.toString().trim { it <= ' ' }
                                    )
                                    //Guardando en Firestore - Enlazado...
                                    FirestoreClass().registerUser(this@RegisterActivity, user)

                                            //Cierra el register y te manda al login
                                            FirebaseAuth.getInstance().signOut()
                                            finish()  /** Se activo de manera obligatoria xk los botones de retroceso
                                     al terminar de registrarse te mandaban al dasboard sin logearse*/

                                } else {
                                    hideProgressDialog()
                                    // If the registering is not successful then show error message
                                    showErrorSnackBar(task.exception!!.message.toString(), true)
                                }
                            })
        }
    }

    //Se enlaza con FirestoreClass
    fun userRegistrationSuccess(){
        //Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            this@RegisterActivity,
            resources.getString(R.string.register_success),
            Toast.LENGTH_SHORT
        ).show()
    }
}