package com.example.sonovhost.ui.fragments_buttomnavig.activ_clienhost

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.example.sonovhost.databinding.ActivityClienHostDetailBinding
import com.example.sonovhost.models.ClienteHostModels
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase



class ClienHostDetailActivity : AppCompatActivity() {

    private lateinit var bindingCHDetailActivity: ActivityClienHostDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingCHDetailActivity = ActivityClienHostDetailBinding.inflate(layoutInflater)
        val view = bindingCHDetailActivity.root
        setContentView(view)

//Se inicia el DetailActivity
        val key = intent.getStringExtra("key")
        val database = Firebase.database
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") val myRef = database.getReference("clientHost").child(
                key.toString()
        )

        myRef.addValueEventListener(object : ValueEventListener{

            //Al ser Intent en el Adapter Jala los datos y lo muestra en el activityDetail
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val mClienHost:ClienteHostModels? = dataSnapshot.getValue(ClienteHostModels::class.java)
                if (mClienHost != null){

                    bindingCHDetailActivity.idDominioClienhostDetailTv.text = mClienHost.dominio.toString()
                    bindingCHDetailActivity.idUsuarioClienhostDetailTv.text = mClienHost.usuario.toString()
                    bindingCHDetailActivity.idDescriptionClienhostDetailTv.text = mClienHost.descripcion.toString()

                    //Esto es LLAMANDO a la clase: GlideLoader MI funcion creada
          /*          mClienHost.url?.let {
                        GlideLoader(this@ClienHostDetailActivity)
                                .loadClienHostPicture(it,id_background_imagen_clienhost_detail_iv)
                    } */

                    //Esto es LLAMANDO a la clase: GlideLoader
          /*         mClienHost.url?.let {
                        GlideLoader(this@ClienHostDetailActivity)
                                .loadUserPicture(it,id_background_imagen_clienhost_detail_iv)
                    } */

                    //Esto es con el modelo de App GameStore sin imagen en el detail
                  Glide.with(getApplicationContext())
                            .load(mClienHost.url.toString())
                            .into(bindingCHDetailActivity.idBackgroundImagenClienhostDetailIv)

                    Glide.with(getApplicationContext())
                            .load(mClienHost.url.toString())
                            .into(bindingCHDetailActivity.idImagenClienhostDetailIv)

                    //Esto es con el modelo de App CrudFirebase con imagen en el detail
                    /*   imagen(mClienHost.url.toString())*/
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG","Failed to read value", error.toException())
            }
        })
    }

}

    //Esto es con el modelo de App CrudFirebase con imagen en el detail
/*    private fun imagen(url:String){
        Glide.with(this)
                .load(url)
                .into(id_imagen_clienhost_detail_iv)
        Glide.with(this)
                .load(url)
                .into(id_background_imagen_clienhost_detail_iv)
    } */


