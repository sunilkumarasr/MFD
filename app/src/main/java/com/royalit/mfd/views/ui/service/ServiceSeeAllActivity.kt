package com.royalit.mfd.views.ui.service

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.royalit.mfd.R
import com.royalit.mfd.adapters.ServiceHomeAdapter
import com.royalit.mfd.databinding.ActivityAboutBinding
import com.royalit.mfd.databinding.ActivityServiceSeeAllBinding
import com.royalit.mfd.views.home.AppViewModel

class ServiceSeeAllActivity : AppCompatActivity() {

    lateinit var binding: ActivityServiceSeeAllBinding

    lateinit var serviceHomeAdapter: ServiceHomeAdapter
    val sharedViewModel:AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityServiceSeeAllBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedViewModel.getCategories()

        serviceHomeAdapter=ServiceHomeAdapter()
        binding.recyclerviewCategories.layoutManager=
            GridLayoutManager(this@ServiceSeeAllActivity.applicationContext,3)
        binding.recyclerviewCategories.adapter=serviceHomeAdapter
        sharedViewModel.categories.observe(this@ServiceSeeAllActivity, Observer {
            Log.d("Categories List","Categories List ${it.size}" )
            serviceHomeAdapter.setData(it)
        })

        binding.lnrBack.setOnClickListener {
            finish()
        }

    }
}