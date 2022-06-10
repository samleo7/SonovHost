package com.example.sonovhost.ui.fragments_buttomnavig.activ_clienhost

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.sonovhost.R
import com.example.sonovhost.databinding.ActivityClienHostEditBinding
import com.example.sonovhost.models.ClienteHostModels
import com.example.sonovhost.utils.Constants
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_clien_host_edit.*
import java.util.*


class ClienHostEditActivity : AppCompatActivity() {

    private lateinit var bindingChEditActiv:ActivityClienHostEditBinding
    private val file = 1
    private var fileUri: Uri? = null
    private var imageUrl:String? = null

    private lateinit var mClientHost:ClienteHostModels

    // Declarando la variable: FECHA para que sea inicializada cuando se lo llame
    private val c = Calendar.getInstance()
    val fechRegistr by lazy {
        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, y, m, d ->
            id_fech_regist_ch_edit_et.setText("$d/${m+1}/$y") //otra forma:(""+d+ "/"+(m+1)+ "/"+y)
        },c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
    }

    val fechVencim by lazy {
        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, y, m, d ->
            id_fech_vencim_ch_edit_et.setText("$d/${m+1}/$y") //otra forma:(""+d+ "/"+(m+1)+ "/"+y)
        },c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingChEditActiv = ActivityClienHostEditBinding.inflate(layoutInflater)
        val view = bindingChEditActiv.root
        setContentView(view)

        bindingChEditActiv.idFechRegistChEditEt.setOnClickListener { fechRegistr.show() }
        bindingChEditActiv.idFechVencimChEditEt.setOnClickListener { fechVencim.show() }

        mClientHost = ClienteHostModels()

        //Inicio EDIT
        val key = intent.getStringExtra("key")
        val database = Firebase.database
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") val myRef = database.getReference("clientHost").child(
                key.toString()
        )

        myRef.addValueEventListener(object : ValueEventListener {

            //Al ser Intent en el Adapter Jala los datos a la activityEdit y lo pone como editable
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val mClienHost:ClienteHostModels?  = dataSnapshot.getValue(ClienteHostModels::class.java)
                if (mClienHost != null){

                    bindingChEditActiv.idDominioChEditEt.text = Editable.Factory.getInstance().newEditable(mClienHost.dominio)
                    bindingChEditActiv.idUsuarioChEditEt.text = Editable.Factory.getInstance().newEditable(mClienHost.usuario)
                    bindingChEditActiv.idClaveChEditEt.text = Editable.Factory.getInstance().newEditable(mClienHost.clave)
                    bindingChEditActiv.idFechRegistChEditEt.text = Editable.Factory.getInstance().newEditable(mClienHost.fech_regist)
                    bindingChEditActiv.idFechVencimChEditEt.text = Editable.Factory.getInstance().newEditable(mClienHost.fech_vencim)
                    bindingChEditActiv.idEstadoEnviadomsntoogChEditTv.text = mClienHost.estado_msn_swc
                    bindingChEditActiv.idRedsocialEditEt.text = Editable.Factory.getInstance().newEditable(mClienHost.redsocial)
                    bindingChEditActiv.idDescriptionEditEt.text = Editable.Factory.getInstance().newEditable(mClienHost.descripcion)
                    bindingChEditActiv.idCorreoChEditEt.text = Editable.Factory.getInstance().newEditable(mClienHost.correo)
                    bindingChEditActiv.idCelularChEditEt.text = Editable.Factory.getInstance().newEditable(mClienHost.celular)

                    imageUrl = mClienHost.url.toString()
                    if(fileUri == null){
                        Glide.with(applicationContext)    // --- habia (view), pero explosionaba
                                .load(imageUrl)
                                .into(bindingChEditActiv.idImagenClienhostEditIv)
                    }

                    //Activacion sincronizada  del RadioButton
                    /**Permitira jalar de firebase y mostrar el radio buttom segun su estado*/

                    if(mClienHost.estado_renov_host ==  Constants.SI_RENOVADO_HOST){
                        id_radiogrou_estad_si_renov_ch_edit_rg.isChecked = true
                    }else {
                        id_radiogrou_estad_no_renov_ch_edit_rg.isChecked = true
                    }

                    //Activacion sincronizada del ToggleButton
                    /**Permitira jalar de firebase y mostrar el icono segun su estado*/
                    if(id_estado_enviadomsntoog_ch_edit_tv.text ==  Constants.ENVIADO){
                        id_btntoogle_estad_icomsn_ch_edit_tb.isChecked = true
                    }else if (id_estado_enviadomsntoog_ch_edit_tv.text  ==  Constants.NO_ENVIADO){
                        id_btntoogle_estad_icomsn_ch_edit_tb.isChecked = false
                    }

                    //ToggleButton - Pase a EnviarSmsWassCorrActivity con sus datos
                    bindingChEditActiv.idBtntoogleEstadIcomsnChEditTb.setOnClickListener {v ->

                        /* Solo cambia su estado en el textView segun su clip - No Guarda en firebase estos text
                           Lo que guarda son los string que esta con Constants*/
                        if(id_btntoogle_estad_icomsn_ch_edit_tb.isChecked){
                            id_estado_enviadomsntoog_ch_edit_tv.setText(R.string.msn_enviado)
                        } else{
                            id_estado_enviadomsntoog_ch_edit_tv.setText(R.string.msn_no_enviado)
                        }

                        val intent = Intent(v.context, EnviarSmsWassCorrActivity::class.java).apply {
                            putExtra("key", mClienHost.key)  // es opcional no afecta en nada
                            putExtra("usuario", mClienHost.usuario)
                            putExtra("celular", mClienHost.celular)
                            putExtra("correo", mClienHost.correo)
                        }

                        v.context.startActivity(intent)
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value", error.toException())
            }
        })

        //Se guarda los cambios Editados
        bindingChEditActiv.idSaveButtonClienthostEdit.setOnClickListener {
            val dominio = bindingChEditActiv.idDominioChEditEt.text.toString()
            val usuario = bindingChEditActiv.idUsuarioChEditEt.text.toString()
            val clave = bindingChEditActiv.idClaveChEditEt.text.toString()
            val fechRegist = bindingChEditActiv.idFechRegistChEditEt.text.toString()
            val fechVencim = bindingChEditActiv.idFechVencimChEditEt.text.toString()

            //Das click al icono y lo que guarda es el string del Constants
            val estado_msn_swc = if (id_btntoogle_estad_icomsn_ch_edit_tb.isChecked){
                Constants.ENVIADO
            } else{
                Constants.NO_ENVIADO
            }

            //Das click al radiobutton y lo que guarda es el string del Constants
            val estado_renov_host = if(id_radiogrou_estad_si_renov_ch_edit_rg.isChecked){
                Constants.SI_RENOVADO_HOST
            } else{
                Constants.NO_RENOVADO_HOST
            }

            val redSocial = bindingChEditActiv.idRedsocialEditEt.text.toString()
            val descripcion = bindingChEditActiv.idDescriptionEditEt.text.toString()
            val correo = bindingChEditActiv.idCorreoChEditEt.text.toString()
            val celular = bindingChEditActiv.idCelularChEditEt.text.toString()
            val folder: StorageReference = FirebaseStorage.getInstance().reference.child("clientHost")
            val clienHostReference: StorageReference = folder.child("img$key")

            if (fileUri == null){

                val mClienHost = ClienteHostModels(
                        dominio, usuario, clave, fechRegist,
                        fechVencim, estado_msn_swc, estado_renov_host,
                        redSocial, descripcion, correo, celular, imageUrl)
                myRef.setValue(mClienHost)

                Toast.makeText(this,"Datos actualizados correctamente", Toast.LENGTH_SHORT).show()

            } else {
                clienHostReference.putFile(fileUri!!).addOnSuccessListener {
                    clienHostReference.downloadUrl.addOnSuccessListener { uri ->

                        val mClienHost = ClienteHostModels(
                                dominio, usuario, clave, fechRegist,
                                fechVencim, estado_msn_swc, estado_renov_host,
                                redSocial, descripcion, correo, celular, uri.toString())
                        myRef.setValue(mClienHost)
                    }
                }

                Toast.makeText(this,"Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
            }

            finish()
        }

        bindingChEditActiv.idImagenClienhostEditIv.setOnClickListener {
            fileUpload()
        }

    }


    private fun fileUpload(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type ="*/*"
        startActivityForResult(intent, file)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == file){
            if (resultCode == RESULT_OK){
                fileUri = data!!.data
                bindingChEditActiv.idImagenClienhostEditIv.setImageURI(fileUri)
            }
        }
    }
}