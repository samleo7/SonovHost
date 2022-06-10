package com.example.sonovhost

import android.content.Intent
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sonovhost.models.ClienteHostModels
import com.example.sonovhost.ui.fragments_buttomnavig.activ_clienhost.ClienHostDetailActivity
import com.example.sonovhost.ui.fragments_buttomnavig.activ_clienhost.ClienHostEditActivity
import com.example.sonovhost.utils.Constants
import kotlinx.android.synthetic.main.card_view_content.view.*
import java.text.SimpleDateFormat
import java.util.*

class AdapterClienhostView (private val values: List<ClienteHostModels>):
    RecyclerView.Adapter<AdapterClienhostView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_view_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            holder.bind(values[position])


    override fun getItemCount() = values.size


     class ViewHolder(item:View) : RecyclerView.ViewHolder(item){

         private lateinit var countDownTimer: CountDownTimer

         fun bind(clienhost:ClienteHostModels){

             //Jala los datos de Firebase y lo muestra en el Recycler - CardView
             itemView.id_dominio_cardview_tv.text = clienhost.dominio
             itemView.id_usuario_cardview_tv.text = clienhost.usuario
             itemView.id_clave_cardview_tv.text = clienhost.clave
             itemView.id_fechregist_cardview_tv.text = clienhost.fech_regist
             itemView.id_fechvencim_cardview_tv.text = clienhost.fech_vencim
             itemView.id_estado_enviadomsntoog_cardview_tv.text = clienhost.estado_msn_swc
             itemView.id_estadorenovhost_toog_cardview_tv.text = clienhost.estado_renov_host
             itemView.id_redsocial_cardview_tv.text = clienhost.redsocial

             // No tenia .context ,se agrego por seaca, creo que al poner arregla la subida y baja recycler
             Glide.with(itemView.context).load(clienhost.url).into(itemView.id_imagen_clienthost_iv)

             //DetailActivity
             itemView.setOnClickListener {v ->
                 val intent = Intent(v.context, ClienHostDetailActivity::class.java).apply {
                     putExtra("key", clienhost.key)
                 }
                 v.context.startActivity(intent)
             }

             //EditActivity
             itemView.setOnLongClickListener { v ->
                 val intent = Intent(v.context, ClienHostEditActivity::class.java).apply {
                     putExtra("key", clienhost.key)
                 }
                 v.context.startActivity(intent)
                 true
             }

             //EnviarSmsWassCorrFragment
          /*   itemView.id_btntoggle_iconomsn_cardview_tb.setOnClickListener {
                 it.findNavController().navigate(R.id.enviarSmsWassCorrFragment)
             } */
    /**         itemView.id_btntoggle_iconomsn_cardview_tb.setOnClickListener {v ->
                 val intent = Intent(v.context, EnviarSmsWassCorrActivity::class.java).apply {
                     putExtra("key", clienhost.key)
                 }
                 v.context.startActivity(intent)
             }   */

             setEstadoBtnToggle()

             setEstadoBtnRadio()

             //Con onclick o ejecucion directa de la funcion
        /*     itemView.id_timecaducid_cardview_tv.setOnClickListener { getTimeCaducid() }  */
             getTimeCaducid()

         }

         private fun setEstadoBtnToggle() {
             itemView.id_btntoggle_iconomsn_cardview_tb.isEnabled = false

             if (itemView.id_estado_enviadomsntoog_cardview_tv.text == Constants.ENVIADO){
                 itemView.id_btntoggle_iconomsn_cardview_tb.isChecked = true

             } else if(itemView.id_estado_enviadomsntoog_cardview_tv.text == Constants.NO_ENVIADO){
                 itemView.id_btntoggle_iconomsn_cardview_tb.isChecked = false
             }
         }

         private fun setEstadoBtnRadio() {
             itemView.id_btntoggle_iconoestadohost_cardview_tb.isEnabled = false

             if (itemView.id_estadorenovhost_toog_cardview_tv.text == Constants.SI_RENOVADO_HOST){
                 itemView.id_btntoggle_iconoestadohost_cardview_tb.isChecked = true
             } else if(itemView.id_estadorenovhost_toog_cardview_tv.text == Constants.NO_RENOVADO_HOST){
                 itemView.id_btntoggle_iconoestadohost_cardview_tb.isChecked = false
             }
         }

         private fun getTimeCaducid(){
             val currentTime = Calendar.getInstance().time
             val fv = itemView.id_fechvencim_cardview_tv.text
             val fechVen = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fv.toString())

             //milliseconds
             val tiempCaducid = fechVen.time - currentTime.time
             countDownTimer = object : CountDownTimer(tiempCaducid, 1000){

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

                     itemView.id_timecaducid_cardview_tv.text = "$elapsedDays days, $elapsedHours:$elapsedMinutes:$elapsedSeconds"

                     // ----

                  /*   if ("$elapsedMinutes" == "20"){

                     } */

                     // -----
                 }

                 override fun onFinish() {
                     itemView.id_timecaducid_cardview_tv.text = "Alerta!, Vencido"
                 }

             }.start()

         }

         }
    }
