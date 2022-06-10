package com.example.sonovhost.ui.activities

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.sonovhost.R
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : BaseActivity() {

    //Inicializando el toolbar
    lateinit var toolbar: Toolbar

    //Inicializando para el  Buttom y Drawer Navigation
    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var navController: NavController

/*----------------------------------------ON CREATE-----------------------------------------------------*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

    setToolbar()

    setButtonNavigation()

    setDrawerNavigation()

    setNavigationUpButton()
    }
/*----------------------------------------ON CREATE-----------------------------------------------------*/

//---- TOOLBAR ----
    private fun setToolbar(){

    toolbar = findViewById(R.id.id_toolbar_dashboard)
    setSupportActionBar(toolbar)

    // cambiar color el Toolbar
    supportActionBar!!.setBackgroundDrawable(
            ContextCompat.getDrawable(
                    this@DashboardActivity,
                    R.drawable.app_gradient_color_background))
    }

//---- BOTTOM NAVIGATION ---
    private fun setButtonNavigation(){
    navController = findNavController(R.id.nav_host_fragment)
    nav_view.setupWithNavController(navController)
    }


//---- DRAWER NAVIGATION ---
    private fun setDrawerNavigation() {
    id_navigatview_itemsmenu_dn.menu.getItem(0).isChecked = true /** Sombrea el 1er item menuDrawer */

    NavigationUI.setupWithNavController(id_navigatview_itemsmenu_dn,navController)

    }

// Flecha arriba left  -- solo para que aparezca
    /** Nota: Al agregar el id_drawer_layout aparece el sanguchito*/
    private fun setNavigationUpButton(){
        appBarConfiguration = AppBarConfiguration(navController.graph,id_drawer_navigation_main)
        NavigationUI.setupActionBarWithNavController(this,navController,id_drawer_navigation_main)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController,appBarConfiguration)
    }

 //
    override fun onBackPressed() {
        doubleBackToExit()
    }


}