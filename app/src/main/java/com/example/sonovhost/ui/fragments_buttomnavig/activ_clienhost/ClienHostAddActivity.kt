package com.example.sonovhost.ui.fragments_buttomnavig.activ_clienhost

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.sonovhost.databinding.ActivityClienHostAddBinding
import com.example.sonovhost.models.ClienteHostModels
import com.example.sonovhost.utils.Constants
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_clien_host_add.*
import java.text.SimpleDateFormat
import java.util.*


@RequiresApi(Build.VERSION_CODES.N)
class ClienHostAddActivity : AppCompatActivity() {

    private lateinit var bindingChAddActivity: ActivityClienHostAddBinding
    private val database = Firebase.database
    private val myRef = database.getReference("clientHost")
    private val file = 1
    private var fileUri:Uri? = null

    private lateinit var mClientHost:ClienteHostModels

    private lateinit var countDownTimer: CountDownTimer

    // Declarando la variable: FECHA para que sea inicializada cuando se lo llame
    private val c = Calendar.getInstance()
    val fechRegistr by lazy {
        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, y, m, d ->
            id_fech_regist_ch_add_et.setText("$d/${m+1}/$y") //otra forma:(""+d+ "/"+(m+1)+ "/"+y)
        },c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
    }

    val fechVencim by lazy {
        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, y, m, d ->
            id_fech_vencim_ch_add_et.setText("$d/${m+1}/$y") //otra forma:(""+d+ "/"+(m+1)+ "/"+y)
        },c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingChAddActivity=ActivityClienHostAddBinding.inflate(layoutInflater)
        setContentView(bindingChAddActivity.root)

        setSaveButtonAdd()

        bindingChAddActivity.idFechRegistChAddEt.setOnClickListener { fechRegistr.show() }
        bindingChAddActivity.idFechVencimChAddEt.setOnClickListener { fechVencim.show() }

   //     bindingChAddActivity.idTimeCaducidadAddTv.setOnClickListener { getTimeCaducid() }

        setBtnToogleEstadoMsn()
        mClientHost=ClienteHostModels()

    }

    private fun setBtnToogleEstadoMsn() {
        id_btntoogle_estad_icomsn_ch_add_tb.setOnClickListener {
            /* Solo cambia su estado en el textView segun su clip - No Guarda en firebase estos text
           Lo que guarda son los string que esta con Constants*/
            if (id_btntoogle_estad_icomsn_ch_add_tb.isChecked){
                id_estado_enviadomsntoog_ch_add_tv.text = "Msn Enviado"
            } else{
                id_estado_enviadomsntoog_ch_add_tv.text = "Msn NO Enviado"
            }
        }
    }


    private fun setSaveButtonAdd() {

        bindingChAddActivity.idSaveButtonClienthostAdd.setOnClickListener {

            val dominio = bindingChAddActivity.idDominioClienhostAddEt.text.toString()
            val usuario = bindingChAddActivity.idUsuarioClienhostAddEt.text.toString()
            val clave = bindingChAddActivity.idClaveClienhostAddEt.text.toString()
            val fechRegist = bindingChAddActivity.idFechRegistChAddEt.text.toString()
            val fechVencim = bindingChAddActivity.idFechVencimChAddEt.text.toString()

            //Das click al icono y lo que guarda es el string de del Constants
            val estado_msn_swc = if (id_btntoogle_estad_icomsn_ch_add_tb.isChecked){
                Constants.ENVIADO
            } else{
                Constants.NO_ENVIADO
            }

            //Das click al radioGroup y lo que guarda es el string de del Constants
            val estado_renov_host = if(id_radiogrou_estad_si_renov_ch_add_rg.isChecked){
                Constants.SI_RENOVADO_HOST
            } else{
                Constants.NO_RENOVADO_HOST
            }

            val redSocial = bindingChAddActivity.idRenovadoSiNoTv.text.toString()
            val descripcion = bindingChAddActivity.idDescriptionEt.text.toString()
            val correo = bindingChAddActivity.idCorreoClienhostAddEt.text.toString()
            val celular = bindingChAddActivity.idCelularClienhostAddEt.text.toString()
        //    val fechaCaducidad = bindingChAddActivity.idTimeCaducidadAddTv.text.toString()

            val key = myRef.push().key.toString()
            val folder = FirebaseStorage.getInstance().reference.child("clientHost") //Nombre carpeta
            val clientHostReference = folder.child("img$key")

            if(fechVencim.isEmpty()){
                bindingChAddActivity.idFechVencimChAddEt.error = ""
                    val toast1 =  Toast.makeText(this,"Ingrese Fecha de vencimiento", Toast.LENGTH_SHORT)
                                toast1.setGravity(Gravity.TOP,0,80)
                                toast1.show()
                return@setOnClickListener}

            if(fileUri==null){

                val mClientHost = ClienteHostModels(
                        dominio, usuario, clave, fechRegist,
                        fechVencim, estado_msn_swc, estado_renov_host,
                        redSocial, descripcion, correo, celular
                )
                myRef.child(key).setValue(mClientHost)

                Toast.makeText(applicationContext,"Datos agregados correctamente", Toast.LENGTH_SHORT).show()

            } else {
                clientHostReference.putFile(fileUri!!).addOnSuccessListener {
                    clientHostReference.downloadUrl.addOnSuccessListener { uri ->

                        val mClientHost = ClienteHostModels(
                                dominio, usuario, clave, fechRegist,
                                fechVencim, estado_msn_swc, estado_renov_host,
                                redSocial, descripcion, correo, celular, uri.toString()
                        )
                                myRef.child(key).setValue(mClientHost)
                    }
                }

                Toast.makeText(applicationContext,"Datos agregados correctamente", Toast.LENGTH_SHORT).show()
            }
            finish()
        }

        bindingChAddActivity.idImagenClienhostAddIv.setOnClickListener {
            fileUpload()
        }
    }

    private fun fileUpload() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, file)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == file) {
            if (resultCode == RESULT_OK) {
                fileUri = data!!.data
                bindingChAddActivity.idImagenClienhostAddIv.setImageURI(fileUri)
            }
        }
    }






























    /**   private fun getTimeCaducid(){
    val currentTime = Calendar.getInstance().time
    val fv = bindingChAddActivity.idFechVencimChAddEt.text
    val fechVen = SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()).parse(fv.toString())

    //milliseconds
    val tiempCaducid = fechVen.time - currentTime.time
    countDownTimer = object :CountDownTimer(tiempCaducid, 1000){

    override fun onTick(millisUntilFinished: Long) {
    var diff = millisUntilFinished
    val secondsInMilli: Long = 1000
    val minutesInMilli = secondsInMilli * 60
    val hoursInMilli = minutesInMilli * 60
    val daysInMilli = hoursInMilli * 24

    val elapsedDays = diff/daysInMilli
    diff %= daysInMilli

    val elapsedHours = diff / hoursInMilli
    diff %= hoursInMilli

    val elapsedMinutes = diff / minutesInMilli
    diff %= minutesInMilli

    val elapsedSeconds = diff / secondsInMilli

    id_time_caducidad_add_tv.text = "$elapsedDays days, $elapsedHours:$elapsedMinutes:$elapsedSeconds"

    }

    override fun onFinish() {
    id_time_caducidad_add_tv.text = "Done!"
    }
    }.start()
    }  */

}
