package com.royalit.mfd

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.royalit.mfd.utils.MyPref
import com.royalit.mfd.views.IntroscreenActivity
import com.royalit.mfd.views.LoginActivity
import com.royalit.mfd.views.home.DashboardActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val userdata= MyPref.getUser(applicationContext)
            if(userdata!=null&&userdata.length>10){
                startActivity(Intent(applicationContext, DashboardActivity::class.java))
                finish()
            }else{
                startActivity(Intent(applicationContext, IntroscreenActivity::class.java))
                finish()
            }
        }, 3000)


    }
}