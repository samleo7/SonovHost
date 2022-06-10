package com.example.sonovhost.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ClienteHostModels(
        val dominio:String? = null,
        val usuario:String? = null,
        val clave:String? = null,
        val fech_regist:String? = null,
        val fech_vencim:String? = null,

        var estado_msn_swc:String? = null,
        val estado_renov_host:String? = null,


        val redsocial:String? = null,
        val descripcion:String? = null,
        val correo:String? = null,
        val celular:String? = null,
 //    val fech_caducidad:String? = null,
        val url: String? = null,
        @Exclude val key: String? = null)