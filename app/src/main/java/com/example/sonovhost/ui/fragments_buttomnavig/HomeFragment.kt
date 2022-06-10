package com.example.sonovhost.ui.fragments_buttomnavig

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.sonovhost.R
import com.example.sonovhost.ui.activities.SettingsActivity


class HomeFragment : Fragment() {

//    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //If we want to use the option menu in fragment we need to add it.
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
//        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)

            textView.text = "This is home Fragment"

        return root
    }


    //Item MENU en el Toolbar
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.bottom_toolbar_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id){
            R.id.action_settings -> {
                //TODO step 9: Launch the settingActivity on click of action item
                //START
                startActivity(Intent(activity, SettingsActivity::class.java))
                //END
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}