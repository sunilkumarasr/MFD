package com.royalit.mfd.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.royalit.mfd.customviews.CustomDialog
import com.royalit.mfd.databinding.ActivityForgotBinding
import com.royalit.mfd.services.RetrofitClient
import com.royalit.mfd.utils.Utils
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotActivity : AppCompatActivity() {

    lateinit var binding: ActivityForgotBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.imgBack.setOnClickListener {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }

    }

    fun send(view: View) {
        forgotpassword()
    }

    fun forgotpassword() {
        // val userdata=Gson().fromJson<LoginResponse>(userdataStr.toString(),LoginResponse::class.java)
        lateinit var user_id: String;
        var email = binding.edittextPhone.text.toString().trim();

        //Log.d("User data","User data ${userdata!!.toString()}}")

        if (email.isEmpty() || !isValidEmail(email)) {
            Utils.showMessage("Enter valid email address", applicationContext)
            return
        }

        var hashMap = HashMap<String, String>(2)
        hashMap.putIfAbsent("email", email);

        if (!Utils.checkConnectivity(applicationContext)) {
            Utils.showMessage("Please check your connection ", applicationContext)
            return
        }

        //showMessage("Calling ",applicationContext)
        val customDialog = CustomDialog(applicationContext);
        customDialog.showDialog(this@ForgotActivity, true)
        RetrofitClient.apiInterface.forgotPassword(hashMap).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                customDialog.closeDialog()
                Log.d("strRes dssad asdas das ", "asdsadas dasdasd " + response.message());
                var strRes = response.body();
                //strRes=strRes.replace("!!","")
                Log.d("strRes ", strRes.toString());
                val jsonObject: JsonObject? = strRes?.let { Utils.getJsonObject(it) }

                Utils.showMessage(jsonObject!!.get("message").asString, applicationContext)
                if (jsonObject!!.get("status").asString == "200") {
                    startActivity(Intent(applicationContext, LoginActivity::class.java))
                    finish()
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                customDialog.closeDialog()
                Utils.showMessage("Try again", applicationContext)
            }
        }
        )
    }

    fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }


    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(applicationContext, LoginActivity::class.java))
        finish()
    }
}