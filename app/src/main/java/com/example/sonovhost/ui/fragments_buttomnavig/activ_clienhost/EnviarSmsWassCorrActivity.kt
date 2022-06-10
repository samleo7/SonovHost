package com.example.sonovhost.ui.fragments_buttomnavig.activ_clienhost

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.sonovhost.R
import com.example.sonovhost.models.ClienteHostModels
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_enviar_sms_wass_corr.*

class EnviarSmsWassCorrActivity : AppCompatActivity() {

    private lateinit var key:String
    private lateinit var database:FirebaseDatabase
    private lateinit var myRef:DatabaseReference

    private lateinit var msnTexto: EditText
    private lateinit var numerocel: EditText
    private lateinit var nombreContacto:EditText
    private lateinit var btnSms: Button
    private lateinit var btnWassapt: Button
    private lateinit var floatingSeleContac: FloatingActionButton

    private lateinit var sendIntent: Intent
    private lateinit var uri:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enviar_sms_wass_corr)

        msnTexto = id_msj_texto_enviar_swc_et
        nombreContacto = id_nombre_contacto_enviar_swc_et
        numerocel = id_numero_celular_enviar_swc_et
        btnSms = id_btn_sms_enviar_swc
        btnWassapt = id_btn_wassapt_enviar_swc
        floatingSeleContac = id_floating_selec_contact_enviar_swc

        //Permiso al telefono para uso del SMS
        if (ActivityCompat.checkSelfPermission(this@EnviarSmsWassCorrActivity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this@EnviarSmsWassCorrActivity, arrayOf(Manifest.permission.SEND_SMS), 1)
        }

        setBtnSmsEnviar()
        setFloatingSelecContac()

        setBtnWassapptEnviar()

        setBtnEmailEnviar()

        getDataFirebase()

       getDataPutExtraCHEdit()

    }

    // ---------------------------Recibe los datos de otra activity: ClienHostActivity ------------------------------------------
    private fun getDataPutExtraCHEdit() {

        val intent = intent
        val usuario = intent.getStringExtra("usuario")
        val celular = intent.getStringExtra("celular")
        val correo = intent.getStringExtra("correo")

        id_nombre_contacto_enviar_swc_et.setText(usuario)
        id_numero_celular_enviar_swc_et.setText(celular)
        id_to_correo_enviar_swc_et.setText(correo)

    }

    // -------------------------------- FireBase -------------------- Funciona solo  con el Adapter

    private fun getDataFirebase() {
        key = intent.getStringExtra("key").toString()
        database = Firebase.database
        myRef = database.getReference("clientHost").child(key)

        myRef.addValueEventListener(object : ValueEventListener {

            //Al ser Intent en el Adapter Jala los datos y lo muestra en los id de los EditText del XML
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val mClienHost = dataSnapshot.getValue(ClienteHostModels::class.java)
                if(mClienHost != null){
                    id_nombre_contacto_enviar_swc_et.setText(mClienHost.usuario.toString())
                    id_numero_celular_enviar_swc_et.setText(mClienHost.celular.toString())
                    id_to_correo_enviar_swc_et.setText(mClienHost.correo.toString())
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG","Failed to read value", error.toException())

            }
        })
    }

    //---------------------------------------------------------------------------------


    //SelecContacto
    private fun setFloatingSelecContac() {
        floatingSeleContac.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            startActivityForResult(intent, 1)
        }
    }


    //Enviar Sms
    private fun setBtnSmsEnviar() {
        btnSms.setOnClickListener {
            if(msnTexto.text.toString().isEmpty() || numerocel.text.toString().isEmpty()){
                Toast.makeText(this@EnviarSmsWassCorrActivity, "Ingrese el Mensaje y el Numero de Celular...", Toast.LENGTH_LONG).show()

            } else{

                try {
                    val smsManager = SmsManager.getDefault()
                    smsManager.sendTextMessage(numerocel.text.toString(), null, msnTexto.text.toString(), null, null)
                    Toast.makeText(this@EnviarSmsWassCorrActivity, "SMS Enviado", Toast.LENGTH_LONG).show()
                }catch (e: Exception){
                    Toast.makeText(this@EnviarSmsWassCorrActivity, "Error SMS: Dar Permiso...\n O ingrese un Numero Valido", Toast.LENGTH_LONG).show()
                }
            }

        }
    }

    //Enviar Sms Wassappt
    private fun setBtnWassapptEnviar() {

        btnWassapt.setOnClickListener {

            if (numerocel.text.toString().isEmpty()){

                try {
                    sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(Intent.EXTRA_TEXT, msnTexto.text.toString())
                    sendIntent.type = "text/plain"
                    sendIntent.setPackage("com.whatsapp")
                    startActivity(sendIntent)
                }catch (e: Exception){
                    Toast.makeText(this@EnviarSmsWassCorrActivity, "Error de Whatsapp.\n Instale la App", Toast.LENGTH_LONG).show()
                }

            } else {

                try {
                    sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_VIEW  // + "+51"
                    uri = "whatsapp://send?phone=" + "+51" + numerocel.text.toString()+"&text="+msnTexto.text.toString()
                    sendIntent.data = Uri.parse(uri)
                    startActivity(sendIntent)
                }catch (e: Exception){
                    Toast.makeText(this@EnviarSmsWassCorrActivity, "Error de Whatsapp.\nInstale la App", Toast.LENGTH_LONG).show()
                }

            }
        }
    }

    //Funcion sobreescrita que permite agregar en nombreContacto y el numero del cel en los EditText
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK){

            val uri = data?.data
            val cursor = contentResolver.query(uri!!, null, null, null, null)

            if (cursor != null && cursor.moveToFirst()){

                val indiceName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val indiceNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                val nombre = cursor.getString(indiceName)
                var numero = cursor.getString(indiceNumber)
                numero = numero.replace("(","").replace(")","").replace("","").replace("-","")
                nombreContacto.setText(nombre)
                numerocel.setText(numero)
            }
        }

    }


    //Enviar Email a Gmail
    private fun setBtnEmailEnviar() {

        id_btn_send_enviar_swc.setOnClickListener{

            //get input from EditTexts and save in variables
            val toCorreo = id_to_correo_enviar_swc_et.text.toString().trim()
            val asunto = id_asunto_enviar_swc_et.text.toString().trim()
            val message = id_message_enviar_swc_et.text.toString().trim()

            sendEmail(toCorreo, asunto, message)
        }
    }

    private fun sendEmail(toCorreo: String, asunto: String, message: String) {

        /* ACTION_SEND action to launch an email client installed on your Android device*/
        val intent = Intent(Intent.ACTION_SEND)
        /* To send an email you need to specify mailto: as URI using setData() method
         and data type will be to text/plain using setType() method*/
        intent.data = Uri.parse("mailto:")
        intent.type = "text/plain"

        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(toCorreo))
        intent.putExtra(Intent.EXTRA_SUBJECT, asunto)
        intent.putExtra(Intent.EXTRA_TEXT, message)

        try {
            startActivity(Intent.createChooser(intent,"Choose email client..."))
        }
        catch (e: Exception){
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()

        }

    }





}