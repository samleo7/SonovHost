package com.example.sonovhost.ui.fragments_buttomnavig

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.sonovhost.AdapterClienhostView
import com.example.sonovhost.databinding.FragmentClienHostBinding
import com.example.sonovhost.models.ClienteHostModels
import com.example.sonovhost.ui.fragments_buttomnavig.activ_clienhost.ClienHostAddActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_clien_host_add.*
import kotlinx.android.synthetic.main.card_view_content.*
import kotlinx.android.synthetic.main.fragment_clien_host.*


class ClienHostFragment : Fragment(){

    private lateinit var bindingClienHostFragment: FragmentClienHostBinding
    private lateinit var messagesListener: ValueEventListener

    private val database = Firebase.database
    private val listClienHost: MutableList<ClienteHostModels> = ArrayList()
    private val myRef = database.getReference("clientHost")

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        bindingClienHostFragment = FragmentClienHostBinding.inflate(inflater)
        return (bindingClienHostFragment.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

 /*       id_floating_add_clienhost_btn.setOnClickListener{
            it.findNavController().navigate(R.id.clienHostAddFragment)
        }    */

        // Navegar de  Fragment a Activity
        bindingClienHostFragment.idFloatingAddClienhostBtn.setOnClickListener { v ->
            val intent = Intent(context, ClienHostAddActivity::class.java)
            v.context.startActivity(intent)
        }

        listClienHost.clear()
        setupRecyclerView(bindingClienHostFragment.idRecyclerviewClienhostFragm)
    }

    private fun setupRecyclerView(recyclerView:RecyclerView) {

        messagesListener = object : ValueEventListener{


            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listClienHost.clear()
                //Recorre la tabla en Firebase y esta preparado para mostrar en el adapter segun la necesidad
                dataSnapshot.children.forEach { resp ->
                    val mClienHost =
                            ClienteHostModels(
                                    resp.child("dominio").getValue() as String?,
                                    resp.child("usuario").getValue() as String?,
                                    resp.child("clave").getValue() as String?,
                                    resp.child("fech_regist").getValue() as String?,
                                    resp.child("fech_vencim").getValue() as String?,
                                    resp.child("estado_msn_swc").getValue() as String?,
                                    resp.child("estado_renov_host").getValue()as String?,
                                    resp.child("redsocial").getValue() as String?,
                                    resp.child("descripcion").getValue() as String?,
                                    resp.child("correo").getValue() as String?,
                                    resp.child("celular").getValue() as String?,
                                    resp.child("url").getValue() as String?,
                                    resp.key)
                    mClienHost.let { listClienHost.add(it) }
                }
                recyclerView.adapter = AdapterClienhostView(listClienHost)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "messages:onCancelled: ${error.message}")
            }
        }
        myRef.addValueEventListener(messagesListener)

        deleteSwipe(recyclerView)
    }

    private fun deleteSwipe(recyclerView:RecyclerView){

        val touchHelperCallback: ItemTouchHelper.SimpleCallback = object: ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val mAlertDialog = AlertDialog.Builder(activity)
                mAlertDialog.setTitle("Confirmación!!")
                mAlertDialog.setMessage("Estas seguro eliminar?")

                mAlertDialog.setPositiveButton("Si"){dialog,id ->

                    val imageFirebaseStorage = FirebaseStorage.getInstance().reference.child(
                            "clientHost/img"+listClienHost[viewHolder.adapterPosition].key)
                    imageFirebaseStorage.delete()


                    listClienHost[viewHolder.adapterPosition].key?.let { myRef.child(it).setValue(null) }
                    listClienHost.removeAt(viewHolder.adapterPosition)

                    recyclerView.adapter?.notifyItemRemoved(viewHolder.adapterPosition)
                    recyclerView.adapter?.notifyDataSetChanged()

                }
                mAlertDialog.setNegativeButton("No"){dialog,id ->
             /*       listClienHost.clear()
                    listClienHost.add(ClienteHostModels()) */
                    recyclerView.adapter?.notifyDataSetChanged()
                    dialog.dismiss()
                }
               mAlertDialog.show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(touchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

}